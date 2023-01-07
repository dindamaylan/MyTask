package com.scrumteam.mytask.data.repository.task

import com.scrumteam.mytask.data.model.task.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTask(): Flow<Result<List<Task>>>

    suspend fun insertTask(task: Task): Result<Boolean>

}