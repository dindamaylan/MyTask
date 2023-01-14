package com.scrumteam.mytask.ui.notification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.scrumteam.mytask.data.model.notification.Notification
import com.scrumteam.mytask.data.repository.notification.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notificationState = MutableStateFlow(NotificationUiState())
    val notificationState: LiveData<NotificationUiState> = _notificationState.asLiveData()

    init {
        loadAllNotification()
    }

    private fun loadAllNotification() {
        viewModelScope.launch {
            notificationRepository.getAllNotification().collect { result ->
                result.onSuccess { notifications ->
                    _notificationState.update {
                        it.copy(isError = false, notifications = notifications)
                    }
                }.onFailure {
                    _notificationState.update {
                        it.copy(isError = true, notifications = emptyList())
                    }
                }
            }
        }
    }

    fun readNotification(notification: Notification) {
        viewModelScope.launch {
            notificationRepository.readNotification(notification.id)
        }
    }

    fun readAllNotification() {
        viewModelScope.launch {
            notificationRepository.readAllNotification()
                .onSuccess { Log.d("TAG", "readAllNotification: Success") }
                .onFailure { e -> Log.d("TAG", "readAllNotification: $e") }
        }
    }
}