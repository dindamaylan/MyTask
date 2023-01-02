package com.scrumteam.mytask.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.scrumteam.mytask.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser.asLiveData()

    private val _changePasswordState: MutableStateFlow<ChangePasswordUiState> = MutableStateFlow(
        ChangePasswordUiState()
    )
    val changePasswordState: LiveData<ChangePasswordUiState> = _changePasswordState.asLiveData()

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun changeNewPassword(newPassword: String) {
        viewModelScope.launch {
            _changePasswordState.update {
                it.copy(isLoading = true, isError = false, isSuccess = false)
            }
            authRepository.changePassword(newPassword)
                .onSuccess {
                    _changePasswordState.update {
                        it.copy(isLoading = false, isError = false, isSuccess = true)
                    }
                }
                .onFailure {
                    _changePasswordState.update {
                        it.copy(isLoading = false, isError = true, isSuccess = false)
                    }
                }
        }
    }
}