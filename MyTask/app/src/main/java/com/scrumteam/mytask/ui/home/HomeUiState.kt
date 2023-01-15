package com.scrumteam.mytask.ui.home

import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.utils.UiText

data class UserUiState(
    val isError: Boolean = false,
    val firebaseUser: FirebaseUser? = null
)

data class LoadTaskUiState(
    val isError: Boolean = false,
    val tasks: List<Task> = emptyList()
)

data class CheckedTask(
    val isError: Boolean = false,
    val isSuccess: Boolean= false,
    val message: UiText
)