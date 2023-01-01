package com.scrumteam.mytask.ui.home

import com.google.firebase.auth.FirebaseUser

data class UserUiState(
    val isError: Boolean = false,
    val firebaseUser: FirebaseUser? = null
)