package com.scrumteam.mytask.ui.auth.login

import com.google.firebase.auth.FirebaseUser

data class LoginUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val currentUser: FirebaseUser? = null
)
