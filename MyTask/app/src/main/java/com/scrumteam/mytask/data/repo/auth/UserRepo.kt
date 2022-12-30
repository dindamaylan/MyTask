package com.scrumteam.mytask.data.repo.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.data.model.User
import com.scrumteam.mytask.utils.UiText
import com.scrumteam.mytask.utils.Result
import kotlinx.coroutines.flow.Flow

interface UserRepo {

    val currentUser: Flow<FirebaseUser>

    suspend fun checkUserIsExists(userId: String): Boolean

    fun getUser(userId: String): Flow<User>

    fun addUser(firebaseUser: FirebaseUser): Flow<Result<UiText>>

    fun loginWithEmailPassword(email: String, password: String): Flow<Result<UiText>>

    fun loginWithGoogle(credential: AuthCredential): Flow<Result<UiText>>

    fun register(
        first_name: String,
        last_name: String,
        email: String,
        password: String
    ): Flow<Result<UiText>>

    fun logout(): Flow<Result<UiText>>
}