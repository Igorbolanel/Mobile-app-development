package com.example.listapp.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.listapp.R
import com.example.listapp.data.AppDatabase
import com.example.listapp.data.DateFormatter
import com.example.listapp.data.Task
import com.example.listapp.data.TaskDao
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.Calendar

class TaskEditActivity : AppCompatActivity() {

    private lateinit var taskDao: TaskDao
    private lateinit var titleInput: EditText
    private lateinit var noteInput: EditText
    private lateinit var deadlineText: TextView
    private lateinit var clearDeadlineButton: ImageButton
    private lateinit var doneButton: Button
    private var taskId = -1
    private var task: Task? = null
    private var deadline: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskDao = AppDatabase.getDatabase(this).taskDao()
        taskId = intent.getIntExtra(EXTRA_TASK_ID, -1)

        titleInput = findViewById(R.id.titleInput)
        noteInput = findViewById(R.id.noteInput)
        deadlineText = findViewById(R.id.deadlineText)
        clearDeadlineButton = findViewById(R.id.clearDeadlineButton)
        doneButton = findViewById(R.id.doneButton)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val deadlineRow = findViewById<LinearLayout>(R.id.deadlineRow)
        val deleteButton = findViewById<Button>(R.id.deleteButton)

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_DEADLINE)) {
            deadline = savedInstanceState.getLong(KEY_DEADLINE)
        }
        showDeadline()

        if (taskId != -1) {
            deleteButton.visibility = View.VISIBLE
            lifecycleScope.launch {
                val loadedTask = taskDao.getById(taskId)
                if (loadedTask == null) {
                    finish()
                } else {
                    task = loadedTask
                    if (savedInstanceState == null) {
                        titleInput.setText(loadedTask.title)
                        noteInput.setText(loadedTask.note)
                        deadline = loadedTask.deadline
                        showDeadline()
                    }
                }
            }
        }

        backButton.setOnClickListener { finish() }
        doneButton.setOnClickListener { saveTask() }
        deadlineRow.setOnClickListener { showDatePicker() }
        clearDeadlineButton.setOnClickListener {
            deadline = null
            showDeadline()
        }
        deleteButton.setOnClickListener { showDeleteDialog() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentDeadline = deadline
        if (currentDeadline != null) {
            outState.putLong(KEY_DEADLINE, currentDeadline)
        }
    }

    private fun showDeadline() {
        val currentDeadline = deadline
        if (currentDeadline == null) {
            deadlineText.setText(R.string.no_deadline)
            clearDeadlineButton.visibility = View.GONE
        } else {
            deadlineText.text = DateFormatter.format(currentDeadline)
            clearDeadlineButton.visibility = View.VISIBLE
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDeadline = deadline
        if (currentDeadline != null) {
            calendar.timeInMillis = currentDeadline
        }

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth, 0, 0, 0)
                selectedDate.set(Calendar.MILLISECOND, 0)
                deadline = selectedDate.timeInMillis
                showDeadline()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun saveTask() {
        val title = titleInput.text.toString().trim()
        if (title.isEmpty()) {
            titleInput.error = getString(R.string.enter_title)
            return
        }
        val note = noteInput.text.toString().trim()
        doneButton.isEnabled = false

        lifecycleScope.launch {
            val currentTask = task
            if (taskId == -1) {
                val newTask = Task(
                    title = title,
                    note = note,
                    deadline = deadline,
                    isDone = false,
                    createdAt = System.currentTimeMillis()
                )
                taskDao.insert(newTask)
            } else if (currentTask != null) {
                taskDao.update(currentTask.copy(title = title, note = note, deadline = deadline))
            }
            finish()
        }
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_question)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ -> deleteTask() }
            .show()
    }

    private fun deleteTask() {
        val currentTask = task
        if (currentTask != null) {
            lifecycleScope.launch {
                taskDao.delete(currentTask)
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
        private const val KEY_DEADLINE = "deadline"
    }
}
