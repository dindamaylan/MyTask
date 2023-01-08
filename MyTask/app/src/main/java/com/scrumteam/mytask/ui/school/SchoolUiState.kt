package com.scrumteam.mytask.ui.school

import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.utils.UiText

data class SchoolUiState(
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val schoolTask: List<Task> = emptyList()
)


data class SchoolCheckedUiState(
    val isError: Boolean = false,
    val isSuccess: Boolean= false,
    val message: UiText
)