package com.scrumteam.mytask.data.repository.notification

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.firestore.util.Util.autoId
import com.scrumteam.mytask.data.model.notification.Notification
import com.scrumteam.mytask.utils.Constants
import com.scrumteam.mytask.utils.Constants.NOTIFICATION_REF
import com.scrumteam.mytask.utils.Constants.READ
import com.scrumteam.mytask.utils.Constants.USER_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : NotificationRepository {

    override fun getAllNotification(): Flow<Result<List<Notification>>> =
        db.collection(NOTIFICATION_REF).whereEqualTo(USER_ID, auth.currentUser?.uid)
            .orderBy(Constants.CREATE_AT, Query.Direction.DESCENDING).snapshots()
            .mapNotNull { snapshot ->
                val data = snapshot.toObjects<Notification>()
                Result.success(data)
            }.catch { e ->
                emit(Result.failure(e))
            }

    override suspend fun insertNotification(notification: Notification): Result<Boolean> {
        return try {
            @SuppressLint("RestrictedApi")
            val id = autoId()
            db.collection(NOTIFICATION_REF).document(id).set(notification.copy(id = id)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun readNotification(notificationId: String): Result<Boolean> {
        return try {
            db.collection(NOTIFICATION_REF).document(notificationId).update(READ, true).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun readAllNotification(): Result<Boolean> {
        return try {
            db.collection(NOTIFICATION_REF).whereEqualTo(USER_ID, auth.currentUser?.uid).snapshots()
                .collect {snapshot ->
                    snapshot.documents.forEach { doc ->
                        doc.reference.update(READ, true)
                    }
                }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}