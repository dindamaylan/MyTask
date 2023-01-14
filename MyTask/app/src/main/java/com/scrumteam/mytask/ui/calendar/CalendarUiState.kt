package com.scrumteam.mytask.ui.calendar

import com.scrumteam.mytask.data.model.task.Task
import java.time.LocalDate

data class CalendarUiState(
    val isError: Boolean = false,
    val tasks: Map<LocalDate, List<Task>> = emptyMap()
)