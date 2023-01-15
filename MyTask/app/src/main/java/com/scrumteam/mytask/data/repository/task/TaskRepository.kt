package com.scrumteam.mytask.data.repository.task

import com.scrumteam.mytask.data.model.task.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTask(): Flow<Result<List<Task>>>

    fun getFilterTask(startDate: Long, endDate: Long): Flow<Result<List<Task>>>

    suspend fun insertTask(task: Task): Result<Boolean>

    suspend fun updateTask(task: Task): Result<Boolean>

    suspend fun deleteTask(task: Task): Result<Boolean>
}