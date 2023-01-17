package com.scrumteam.mytask.ui.school

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
class SchoolViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _schoolState = MutableStateFlow(SchoolUiState())
    val schoolState = _schoolState.asLiveData()

    private val _schoolCheckedState = MutableLiveData<Event<SchoolCheckedUiState>>()
    val schoolCheckedState: LiveData<Event<SchoolCheckedUiState>> = _schoolCheckedState

    init {
        loadSchoolTasks()
    }

    private fun loadSchoolTasks() {
        viewModelScope.launch {
            taskRepository.getAllTask().collect { result ->
                result.onSuccess { tasks ->
                    val tasksSchool =
                        tasks.filter { task -> task.category == TaskCode.SCHOOL.name }
                    _schoolState.update {
                        it.copy(
                            isError = false,
                            message = if (tasksSchool.isEmpty()) UiText.StringResource(R.string.text_message_data_empty) else null,
                            schoolTask = tasksSchool
                        )
                    }
                }.onFailure {
                    _schoolState.update {
                        it.copy(
                            isError = true,
                            message = UiText.StringResource(R.string.text_message_error_something),
                            schoolTask = emptyList()
                        )

                    }
                }
            }
        }
    }

    fun checkedTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
                .onSuccess {
                    _schoolCheckedState.value = Event(
                        SchoolCheckedUiState(
                            isError = false,
                            isSuccess = true,
                            message = UiText.StringResource(R.string.text_message_success_complete_task)
                        )
                    )
                }
                .onFailure {
                    _schoolCheckedState.value = Event(
                        SchoolCheckedUiState(
                            isError = true,
                            isSuccess = false,
                            message = UiText.StringResource(R.string.text_message_failure_complete_task)
                        )
                    )
                }

        }
    }

    fun deleteSchoolTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}