package com.scrumteam.mytask.ui.notification

import com.scrumteam.mytask.data.model.notification.Notification

data class NotificationUiState(
    val isError: Boolean = false,
    val notifications: List<Notification> = emptyList()
)
