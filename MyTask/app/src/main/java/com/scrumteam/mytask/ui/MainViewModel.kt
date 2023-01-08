package com.scrumteam.mytask.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrumteam.mytask.R
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.data.repository.auth.AuthRepository
import com.scrumteam.mytask.data.repository.task.TaskRepository
import com.scrumteam.mytask.utils.Event
import com.scrumteam.mytask.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _createTaskState = MutableLiveData<Event<CreateTaskUiState>>()
    val createTaskState = _createTaskState

    fun insertTask(task: Task) {
        viewModelScope.launch {
            authRepository.currentUser.first()?.let { currentUser ->
                taskRepository.insertTask(task.copy(userId = currentUser.uid))
                    .onSuccess {
                        _createTaskState.value = Event(
                            CreateTaskUiState(
                                isError = false,
                                isSuccess = true,
                                message = UiText.StringResource(R.string.text_message_success_create_task)
                            )
                        )
                    }
                    .onFailure {
                        _createTaskState.value = Event(
                            CreateTaskUiState(
                                isError = true,
                                isSuccess = false,
                                message = UiText.StringResource(R.string.text_message_failure_create_task)
                            )
                        )
                    }
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            authRepository.currentUser.first()?.let { currentUser ->
                taskRepository.updateTask(task.copy(userId = currentUser.uid))
                    .onSuccess {
                        _createTaskState.value = Event(
                            CreateTaskUiState(
                                isError = false,
                                isSuccess = true,
                                message = UiText.StringResource(R.string.text_message_success_update_task)
                            )
                        )
                    }
                    .onFailure {
                        _createTaskState.value = Event(
                            CreateTaskUiState(
                                isError = true,
                                isSuccess = false,
                                message = UiText.StringResource(R.string.text_message_failure_update_task)
                            )
                        )
                    }
            }
        }
    }
}