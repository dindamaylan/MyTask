package com.scrumteam.mytask.data.repository.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow


interface AuthRepository {

    val currentUser: Flow<FirebaseUser?>

    suspend fun loginWithEmailPassword(email: String, password: String): Result<FirebaseUser>

    suspend fun loginWithCredential(credential: AuthCredential): Result<FirebaseUser>

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<FirebaseUser>

    suspend fun logout()

    suspend fun changePassword(newPassword: String): Result<FirebaseUser>
}