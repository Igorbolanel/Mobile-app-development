package com.example.listapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R
import com.example.listapp.data.AppDatabase
import com.example.listapp.data.Task
import com.example.listapp.data.TaskDao
import com.example.listapp.domain.TaskSearch
import com.google.android.material.card.MaterialCardView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter
    private lateinit var searchInput: EditText
    private lateinit var taskCard: MaterialCardView
    private lateinit var emptyText: TextView
    private lateinit var addButton: FloatingActionButton
    private var tasks: List<Task> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskDao = AppDatabase.getDatabase(this).taskDao()

        searchInput = findViewById(R.id.searchInput)
        taskCard = findViewById(R.id.taskCard)
        emptyText = findViewById(R.id.emptyText)
        addButton = findViewById(R.id.addButton)
        val taskList = findViewById<RecyclerView>(R.id.taskList)
        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)

        adapter = TaskAdapter(
            onTaskClick = { task -> openTask(task) },
            onDoneClick = { task, isDone -> changeDone(task, isDone) }
        )
        taskList.adapter = adapter

        val divider = MaterialDividerItemDecoration(this, MaterialDividerItemDecoration.VERTICAL)
        divider.isLastItemDecorated = false
        taskList.addItemDecoration(divider)

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    deleteTask(adapter.currentList[position])
                }
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(taskList)

        searchInput.doAfterTextChanged {
            showTasks()
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        addButton.setOnClickListener {
            startActivity(Intent(this, TaskEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            loadTasks()
        }
    }

    private suspend fun loadTasks() {
        tasks = taskDao.getAll()
        showTasks()
    }

    private fun showTasks() {
        val searchText = searchInput.text.toString()
        val foundTasks = TaskSearch.filter(tasks, searchText)
        adapter.submitList(foundTasks)

        if (foundTasks.isEmpty()) {
            taskCard.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
            if (searchText.isBlank()) {
                emptyText.setText(R.string.no_tasks)
            } else {
                emptyText.setText(R.string.nothing_found)
            }
        } else {
            taskCard.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
        }
    }

    private fun openTask(task: Task) {
        val intent = Intent(this, TaskEditActivity::class.java)
        intent.putExtra(TaskEditActivity.EXTRA_TASK_ID, task.id)
        startActivity(intent)
    }

    private fun changeDone(task: Task, isDone: Boolean) {
        lifecycleScope.launch {
            taskDao.update(task.copy(isDone = isDone))
            loadTasks()
        }
    }

    private fun deleteTask(task: Task) {
        lifecycleScope.launch {
            taskDao.delete(task)
            loadTasks()
            Snackbar.make(addButton, R.string.task_deleted, Snackbar.LENGTH_LONG)
                .setAnchorView(addButton)
                .setAction(R.string.undo) { restoreTask(task) }
                .show()
        }
    }

    private fun restoreTask(task: Task) {
        lifecycleScope.launch {
            taskDao.insert(task)
            loadTasks()
        }
    }
}
