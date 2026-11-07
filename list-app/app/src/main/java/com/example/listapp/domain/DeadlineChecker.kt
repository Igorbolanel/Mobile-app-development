package com.example.listapp.domain

import java.util.Calendar

object DeadlineChecker {
    fun isOverdue(deadline: Long): Boolean {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        return deadline < today.timeInMillis
    }
}
