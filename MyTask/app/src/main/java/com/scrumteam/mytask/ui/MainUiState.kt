package com.scrumteam.mytask.ui

import com.scrumteam.mytask.utils.UiText

data class CreateTaskUiState(
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val message: UiText
)