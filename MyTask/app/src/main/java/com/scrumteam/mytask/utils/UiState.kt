package com.scrumteam.mytask.utils

data class UiState (
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val message: UiText? = null,
)