package com.scrumteam.mytask.data.model.task

data class Task(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "",
    val date: String = "",
    val time: String = "",
    val isCheck: Boolean = false,
    val createAt: Long = System.currentTimeMillis()
)
