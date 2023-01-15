package com.scrumteam.mytask.ui.work

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
class WorkViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _workState = MutableStateFlow(WorkUiState())
    val workState = _workState.asLiveData()

    private val _workCheckedState = MutableLiveData<Event<WorkCheckedUiState>>()
    val workCheckedState: LiveData<Event<WorkCheckedUiState>> = _workCheckedState


    init {
        loadWorkTask()
    }

    private fun loadWorkTask() {
        viewModelScope.launch {
            _workState.update {
                it.copy(isError = false, isLoading = true, taskByWorks = emptyList())
            }
            taskRepository.getAllTask().collect { result ->
                result.onSuccess { tasks ->
                    _workState.update {
                        it.copy(
                            isError = false,
                            isLoading = false,
                            taskByWorks = tasks.filter { task -> task.category == TaskCode.WORK.name }
                        )
                    }
                }.onFailure {
                    _workState.update {
                        it.copy(isError = true, isLoading = false, taskByWorks = emptyList())
                    }
                }
            }
        }
    }

    fun checkedTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
                .onSuccess {
                    _workCheckedState.value = Event(
                        WorkCheckedUiState(
                            isError = false,
                            isSuccess = true,
                            message = UiText.StringResource(R.string.text_message_success_complete_task)
                        )
                    )
                }
                .onFailure {
                    _workCheckedState.value = Event(
                        WorkCheckedUiState(
                            isError = true,
                            isSuccess = false,
                            message = UiText.StringResource(R.string.text_message_failure_complete_task)
                        )
                    )
                }

        }
    }

    fun deleteWorkTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}