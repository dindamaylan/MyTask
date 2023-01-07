package com.scrumteam.mytask.data.model.task

data class TaskType(
    val codeName: TaskCode,
    val value: String
) {
    override fun toString(): String = value
}
