package com.scrumteam.mytask.data.repository.notification

import com.scrumteam.mytask.data.model.notification.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAllNotification(): Flow<Result<List<Notification>>>

    suspend fun insertNotification(notification: Notification): Result<Boolean>

    suspend fun readNotification(notificationId: String): Result<Boolean>

    suspend fun readAllNotification(): Result<Boolean>
}