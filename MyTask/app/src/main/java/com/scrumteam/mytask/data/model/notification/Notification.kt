package com.scrumteam.mytask.data.model.notification

data class Notification(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val message: String = "",
    val date: Long = 0L,
    val time: Long = 0L,
    val read: Boolean = false,
    val createAt: Long = 0L
)
