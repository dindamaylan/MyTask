package com.scrumteam.mytask.data.model.task

data class Task(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "",
    val date: Long = 0L,
    val time: Long = 0L,
    val checked: Boolean = false,
    val createAt: Long = System.currentTimeMillis()
)
