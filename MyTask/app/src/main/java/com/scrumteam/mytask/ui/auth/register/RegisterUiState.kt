package com.scrumteam.mytask.ui.auth.register

import com.google.firebase.auth.FirebaseUser

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val currentUser: FirebaseUser? = null
)
