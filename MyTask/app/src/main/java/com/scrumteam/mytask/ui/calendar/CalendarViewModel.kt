package com.scrumteam.mytask.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.scrumteam.mytask.data.mapper.toLocalDate
import com.scrumteam.mytask.data.repository.task.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _tasksState = MutableStateFlow(CalendarUiState())
    val tasksState: LiveData<CalendarUiState> = _tasksState.asLiveData()

    init {
        loadAllTasks()
    }

    private fun loadAllTasks() {
        viewModelScope.launch {
            taskRepository.getAllTask().collect { result ->
                result.onSuccess { tasks ->
                    _tasksState.update {
                        it.copy(
                            isError = false,
                            tasks = tasks.groupBy { task ->
                                task.date.toLocalDate()
                            }
                        )
                    }
                }.onFailure {
                    _tasksState.update {
                        it.copy(
                            isError = true,
                            tasks = emptyMap()
                        )
                    }
                }
            }
        }
    }
}