package com.scrumteam.mytask.ui.personal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.scrumteam.mytask.R
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.data.model.task.TaskCode
import com.scrumteam.mytask.data.repository.task.TaskRepository
import com.scrumteam.mytask.utils.Event
import com.scrumteam.mytask.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _personalState = MutableStateFlow(PersonalUiState())
    val personalState = _personalState.asLiveData()

    private val _personalCheckedState = MutableLiveData<Event<PersonalCheckedUiState>>()
    val personalCheckedState: LiveData<Event<PersonalCheckedUiState>> = _personalCheckedState

    init {
        loadPersonalTasks()
    }

    private fun loadPersonalTasks() {
        viewModelScope.launch {
            taskRepository.getAllTask().collect { result ->
                result.onSuccess { tasks ->
                    val tasksPersonal =
                        tasks.filter { task -> task.category == TaskCode.PERSONAL.name }
                    _personalState.update {
                        it.copy(
                            isError = false,
                            message = if (tasksPersonal.isEmpty()) UiText.StringResource(R.string.text_message_data_empty) else null,
                            personalTask = tasksPersonal
                        )
                    }
                }.onFailure {
                    _personalState.update {
                        it.copy(
                            isError = true,
                            message = UiText.StringResource(R.string.text_message_error_something),
                            personalTask = emptyList()
                        )

                    }
                }
            }
        }
    }

    fun checkedTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task).onSuccess {
                _personalCheckedState.value = Event(
                    PersonalCheckedUiState(
                        isError = false,
                        isSuccess = true,
                        message = UiText.StringResource(R.string.text_message_success_complete_task)
                    )
                )
            }.onFailure {
                _personalCheckedState.value = Event(
                    PersonalCheckedUiState(
                        isError = true,
                        isSuccess = false,
                        message = UiText.StringResource(R.string.text_message_failure_complete_task)
                    )
                )
            }

        }
    }


    fun deletePersonalTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}