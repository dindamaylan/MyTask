package com.scrumteam.mytask.ui.work

import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.utils.UiText

data class WorkUiState(
    val isError: Boolean = false,
    val message: UiText? = null,
    val taskByWorks: List<Task> = emptyList()
)

data class WorkCheckedUiState(
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val message: UiText
)