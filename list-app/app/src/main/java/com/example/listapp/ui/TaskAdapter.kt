package com.example.listapp.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.listapp.R
import com.example.listapp.data.DateFormatter
import com.example.listapp.data.Task
import com.example.listapp.domain.DeadlineChecker

class TaskAdapter(
    private val onDoneClick: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doneCheck: CheckBox = view.findViewById(R.id.doneCheck)
        val taskTitle: TextView = view.findViewById(R.id.taskTitle)
        val taskDeadline: TextView = view.findViewById(R.id.taskDeadline)
        val titleColor: Int = taskTitle.currentTextColor
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        val context = holder.itemView.context

        holder.taskTitle.text = task.title
        holder.doneCheck.isChecked = task.isDone

        if (task.isDone) {
            holder.taskTitle.paintFlags = holder.taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.taskTitle.setTextColor(ContextCompat.getColor(context, R.color.gray))
        } else {
            holder.taskTitle.paintFlags = holder.taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.taskTitle.setTextColor(holder.titleColor)
        }

        val deadline = task.deadline
        if (deadline == null) {
            holder.taskDeadline.visibility = View.GONE
        } else {
            holder.taskDeadline.visibility = View.VISIBLE
            holder.taskDeadline.text = context.getString(R.string.deadline_format, DateFormatter.format(deadline))
            if (!task.isDone && DeadlineChecker.isOverdue(deadline)) {
                holder.taskDeadline.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else {
                holder.taskDeadline.setTextColor(ContextCompat.getColor(context, R.color.gray))
            }
        }

        holder.doneCheck.setOnClickListener {
            onDoneClick(task, holder.doneCheck.isChecked)
        }
    }
}
