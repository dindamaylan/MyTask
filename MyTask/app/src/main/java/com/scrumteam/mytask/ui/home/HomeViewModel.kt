package com.scrumteam.mytask.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrumteam.mytask.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userState: MutableLiveData<UserUiState> = MutableLiveData()
    val userState: LiveData<UserUiState> = _userState

    init {
        loadCurrentUser()
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
}