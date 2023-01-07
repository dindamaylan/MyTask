package com.scrumteam.mytask.data.repository.task

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.util.Util.autoId
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.utils.Constants.CREATE_AT
import com.scrumteam.mytask.utils.Constants.TASK_REF
import com.scrumteam.mytask.utils.Constants.USER_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth, private val db: FirebaseFirestore
) : TaskRepository {

    override fun getAllTask(): Flow<Result<List<Task>>> =
        db.collection(TASK_REF).whereEqualTo(USER_ID, auth.currentUser?.uid)
            .orderBy(CREATE_AT, Query.Direction.DESCENDING).snapshots()
            .mapNotNull { snapshot ->
                val data = snapshot.toObjects<Task>()
                Result.success(data)
            }.catch { e ->
                emit(Result.failure(e))
            }

    override suspend fun insertTask(task: Task): Result<Boolean> {
        return try {
            @SuppressLint("RestrictedApi") val id = autoId()
            db.collection(TASK_REF).document(id).set(task.copy(id = id)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}