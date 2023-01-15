package com.scrumteam.mytask.ui.home

import androidx.lifecycle.*
import com.scrumteam.mytask.R
import com.scrumteam.mytask.data.mapper.toInSecond
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.data.repository.auth.AuthRepository
import com.scrumteam.mytask.data.repository.task.TaskRepository
import com.scrumteam.mytask.utils.Event
import com.scrumteam.mytask.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _userState = MutableLiveData<UserUiState>()
    val userState = _userState

    private val _taskState = MutableStateFlow(LoadTaskUiState())
    val taskState = _taskState.asLiveData()

    private val _taskFilterState = MutableStateFlow(LoadTaskUiState())
    val taskFilterState = _taskFilterState.asLiveData()

    private val _taskCheckedState = MutableLiveData<Event<CheckedTask>>()
    val taskCheckedState: LiveData<Event<CheckedTask>> = _taskCheckedState

    init {
        loadCurrentUser()
        loadAllTask()
        loadFilterTask()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.currentUser.collect { firebaseUser ->
                firebaseUser?.let {
                    _userState.value = UserUiState(firebaseUser = it)
                } ?: kotlin.run {
                    _userState.value = UserUiState(isError = true)
                }
            }
        }
    }

    private fun loadAllTask() {
        viewModelScope.launch {
            taskRepository.getAllTask()
                .collect { result ->
                    result.onSuccess { tasks ->
                        _taskState.update {
                            it.copy(isError = false, tasks = tasks)
                        }
                    }.onFailure {
                        _taskState.update {
                            it.copy(isError = true, tasks = emptyList())
                        }
                    }
                }
        }
    }

    private fun loadFilterTask() {
        viewModelScope.launch {
            val startDate = LocalDateTime.now().minusDays(3)
            val endDate = LocalDateTime.now().plusDays(3)
            taskRepository.getFilterTask(startDate.toInSecond(), endDate.toInSecond())
                .collect { result ->
                    result.onSuccess { tasks ->
                        _taskFilterState.update {
                            it.copy(isError = false, tasks = tasks)
                        }
                    }.onFailure {
                        _taskFilterState.update {
                            it.copy(isError = true, tasks = emptyList())
                        }
                    }
                }
        }
    }

    fun checkedTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
                .onSuccess {
                    _taskCheckedState.value = Event(
                        CheckedTask(
                            isError = false,
                            isSuccess = true,
                            message = UiText.StringResource(R.string.text_message_success_complete_task)
                        )
                    )
                }
                .onFailure {
                    _taskCheckedState.value = Event(
                        CheckedTask(
                            isError = true,
                            isSuccess = false,
                            message = UiText.StringResource(R.string.text_message_failure_complete_task)
                        )
                    )
                }

        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}