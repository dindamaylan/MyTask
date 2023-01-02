package com.scrumteam.mytask.ui.auth.login

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.data.model.User
import com.scrumteam.mytask.data.repo.auth.UserRepo
import com.scrumteam.mytask.utils.UiState
import com.scrumteam.mytask.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel (private val userRepo: UserRepo) : ViewModel() {

    private val _loginUiState: MutableLiveData<UiState> = MutableLiveData()
    val loginUiState: LiveData<UiState> = _loginUiState

    private val _logoutUiState: MutableLiveData<UiState> = MutableLiveData()
    val logoutUiState: LiveData<UiState> = _logoutUiState

    val currentUser: LiveData<FirebaseUser> = userRepo.currentUser.asLiveData()

    val getUser: LiveData<User> = Transformations.switchMap(currentUser) {
        userRepo.getUser(it.uid).asLiveData()
    }

    val checkUserIsExists: LiveData<Boolean> =
        Transformations.switchMap(currentUser) {
            liveData {
                val result = userRepo.checkUserIsExists(it.uid)
                emit(result)
            }
        }

    fun loginWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            userRepo.loginWithGoogle(credential).collect { result ->
                when (result) {
                    is Result.Success -> _loginUiState.value =
                        UiState(isSuccess = true, message = result.data)
                    is Result.Loading -> _loginUiState.value = UiState(isLoading = true)
                    is Result.Error -> _loginUiState.value =
                        UiState(isError = true, message = result.uiText)
                }
            }
        }
    }

    fun addUser(): LiveData<UiState> = Transformations.switchMap(currentUser) {
        liveData {
            userRepo.addUser(it).collect { result ->
                when (result) {
                    is Result.Success ->
                        emit(UiState(isSuccess = true, message = result.data))
                    is Result.Loading -> emit(UiState(isLoading = true))
                    is Result.Error ->
                        emit(UiState(isError = true, message = result.uiText))
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepo.loginWithEmailPassword(email, password).collect { result ->
                Log.d("TAG", "login: $result")
                when (result) {
                    is Result.Success -> _loginUiState.value =
                        UiState(isSuccess = true, message = result.data)
                    is Result.Loading -> _loginUiState.value = UiState(isLoading = true)
                    is Result.Error -> _loginUiState.value =
                        UiState(isError = true, message = result.uiText)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepo.logout().collect { result ->
                when (result) {
                    is Result.Success -> _logoutUiState.value =
                        UiState(isSuccess = true, message = result.data)
                    is Result.Loading -> _logoutUiState.value = UiState(isLoading = true)
                    is Result.Error -> _logoutUiState.value =
                        UiState(isError = true, message = result.uiText)
                }
            }
        }
    }
}