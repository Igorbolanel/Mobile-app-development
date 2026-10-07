package com.example.listapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R
import com.example.listapp.data.AppDatabase
import com.example.listapp.data.Task
import com.example.listapp.data.TaskDao
import com.google.android.material.card.MaterialCardView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var taskDao: TaskDao
    private lateinit var adapter: TaskAdapter
    private lateinit var taskCard: MaterialCardView
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskDao = AppDatabase.getDatabase(this).taskDao()

        taskCard = findViewById(R.id.taskCard)
        emptyText = findViewById(R.id.emptyText)
        val taskList = findViewById<RecyclerView>(R.id.taskList)
        val addButton = findViewById<FloatingActionButton>(R.id.addButton)

        adapter = TaskAdapter(
            onTaskClick = { task -> openTask(task) },
            onDoneClick = { task, isDone -> changeDone(task, isDone) }
        )
        taskList.adapter = adapter

        val divider = MaterialDividerItemDecoration(this, MaterialDividerItemDecoration.VERTICAL)
        divider.isLastItemDecorated = false
        taskList.addItemDecoration(divider)

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
        val tasks = taskDao.getAll()
        adapter.submitList(tasks)

        if (tasks.isEmpty()) {
            taskCard.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
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
}
