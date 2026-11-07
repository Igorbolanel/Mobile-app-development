package com.example.listapp.domain

import com.example.listapp.data.Task

object TaskSearch {
    fun filter(tasks: List<Task>, text: String): List<Task> {
        val searchText = text.trim()
        if (searchText.isEmpty()) {
            return tasks
        }
        return tasks.filter { it.title.contains(searchText, ignoreCase = true) }
    }
}
