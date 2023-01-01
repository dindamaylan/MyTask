package com.scrumteam.mytask.ui.auth.register

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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState: MutableStateFlow<RegisterUiState> =
        MutableStateFlow(RegisterUiState())
    val registerState: LiveData<RegisterUiState> = _registerState.asLiveData()

    fun registerWithEmailPassword(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _registerState.update {
                it.copy(isLoading = true, isError = false, currentUser = null)
            }
            authRepository.register(firstName, lastName, email, password)
                .onSuccess { currentUser ->
                    _registerState.update {
                        it.copy(isLoading = false, isError = false, currentUser = currentUser)
                    }
                }
                .onFailure {
                    _registerState.update {
                        it.copy(isLoading = false, isError = true, currentUser = null)
                    }
                }
        }
    }
}