package com.scrumteam.mytask.ui.home

import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.data.model.task.Task

data class UserUiState(
    val isError: Boolean = false,
    val firebaseUser: FirebaseUser? = null
)

data class LoadTaskUiState(
    val isError: Boolean = false,
    val tasks: List<Task> = emptyList()
)