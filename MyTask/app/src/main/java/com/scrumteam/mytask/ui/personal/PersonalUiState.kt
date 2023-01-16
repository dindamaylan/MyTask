package com.scrumteam.mytask.ui.personal

import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.utils.UiText

data class PersonalUiState(
    val isError: Boolean = false,
    val message: UiText? = null,
    val personalTask: List<Task> = emptyList()
)

data class PersonalCheckedUiState(
    val isError: Boolean = false,
    val isSuccess: Boolean= false,
    val message: UiText
)