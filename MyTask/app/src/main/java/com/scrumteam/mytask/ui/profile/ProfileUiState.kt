package com.scrumteam.mytask.ui.profile

data class ChangePasswordUiState(
    val isError: Boolean = false,
    val isSuccess: Boolean = false
)

data class UpdateProfileUiState(
    val isError: Boolean = false,
    val isSuccess: Boolean= false
)