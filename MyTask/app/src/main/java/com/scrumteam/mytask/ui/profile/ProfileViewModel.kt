package com.scrumteam.mytask.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.data.repository.auth.AuthRepository
import com.scrumteam.mytask.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val currentUser = _currentUser

    private val _updateProfileState: MutableLiveData<Event<UpdateProfileUiState>> =
        MutableLiveData()
    val updateProfileState = _updateProfileState


    private val _changePasswordState: MutableLiveData<Event<ChangePasswordUiState>> =
        MutableLiveData()
    val changePasswordState = _changePasswordState

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.currentUser.collect {
                _currentUser.value = it
            }
        }
    }

    fun updateProfileUser(firstName: String, lastName: String, avatarUri: Uri? = null) {
        viewModelScope.launch {
            authRepository.updateProfile(firstName, lastName, avatarUri)
                .onSuccess { currentUser ->
                    _currentUser.value = currentUser
                    _updateProfileState.value =
                        Event(UpdateProfileUiState(isError = false, isSuccess = true))
                }
                .onFailure {
                    _updateProfileState.value =
                        Event(UpdateProfileUiState(isError = true, isSuccess = false))
                }
        }
    }

    fun changeNewPassword(newPassword: String) {
        viewModelScope.launch {
            authRepository.changePassword(newPassword)
                .onSuccess {
                    _changePasswordState.value =
                        Event(ChangePasswordUiState(isError = false, isSuccess = true))
                }
                .onFailure {
                    _changePasswordState.value =
                        Event(ChangePasswordUiState(isError = true, isSuccess = false))
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}