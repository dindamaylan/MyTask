package com.scrumteam.mytask.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.scrumteam.mytask.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser by lazy { authRepository.currentUser.asLiveData() }

    private val _loginState: MutableStateFlow<LoginUiState> = MutableStateFlow(LoginUiState())
    val loginState: LiveData<LoginUiState> = _loginState.asLiveData()

    fun loginWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _loginState.update {
                it.copy(
                    isError = false,
                    isLoading = true,
                    currentUser = null
                )
            }
            authRepository.loginWithEmailPassword(email, password)
                .onSuccess { currentUser ->
                    _loginState.update {
                        it.copy(
                            isError = false,
                            isLoading = false,
                            currentUser = currentUser
                        )
                    }
                }
                .onFailure {
                    _loginState.update {
                        it.copy(
                            isError = true,
                            isLoading = false,
                            currentUser = null
                        )
                    }
                }
        }
    }

    fun loginWithCredential(authCredential: AuthCredential) {
        viewModelScope.launch {
            _loginState.update {
                it.copy(
                    isError = false,
                    isLoading = true,
                    currentUser = null
                )
            }
            authRepository.loginWithCredential(authCredential)
                .onSuccess { currentUser ->
                    _loginState.update {
                        it.copy(
                            isError = false,
                            isLoading = false,
                            currentUser = currentUser
                        )
                    }
                }
                .onFailure {
                    _loginState.update {
                        it.copy(
                            isError = true,
                            isLoading = false,
                            currentUser = null
                        )
                    }
                }
        }
    }

}