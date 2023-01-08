package com.scrumteam.mytask.utils

import android.content.Context
import com.scrumteam.mytask.R
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.data.model.task.TaskCode
import com.scrumteam.mytask.data.model.task.TaskType
import com.scrumteam.mytask.utils.Constants.NULL
import java.time.LocalDate
import java.util.*

fun getTaskTypes(context: Context): List<TaskType> {
    return listOf(
        TaskType(TaskCode.PERSONAL, context.getString(R.string.personal)),
        TaskType(TaskCode.WORK, context.getString(R.string.work)),
        TaskType(TaskCode.SCHOOL, context.getString(R.string.school)),
    )
}

fun List<Task>.getTotalTaskByCategory(taskCode: TaskCode): Int {
    return this.filter { task -> task.category == taskCode.name }.size
}

fun mergeDateTimeWithCategoryTask(date: String, time: String, taskCode: String): String {
    return date.ifEmpty { "" }
        .plus(if (time != NULL) " - $time" else "")
        .plus(if (taskCode.isNotEmpty()) " - $taskCode" else "")
}

fun String.toTaskCodeRes(context: Context): String {
    return when (this) {
        TaskCode.PERSONAL.toString() -> context.getString(R.string.personal)
        TaskCode.WORK.toString() -> context.getString(R.string.work)
        TaskCode.SCHOOL.toString() -> context.getString(R.string.school)
        else -> ""
    }
}