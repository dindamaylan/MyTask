package com.scrumteam.mytask.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.scrumteam.mytask.data.repository.auth.AuthRepository
import com.scrumteam.mytask.data.repository.task.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    init {
        loadCurrentUser()
        loadAllTask()
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
            taskRepository.getAllTask().collect { result ->
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
}