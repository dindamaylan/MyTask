package com.scrumteam.mytask.data.repository.auth

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.scrumteam.mytask.utils.mergeFullName
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
) : AuthRepository {

    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun loginWithEmailPassword(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            auth.currentUser?.let {
                Result.success(it)
            } ?: Result.failure(FirebaseAuthException("", ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithCredential(credential: AuthCredential): Result<FirebaseUser> {
        return try {
            auth.signInWithCredential(credential).await()
            auth.currentUser?.let {
                Result.success(it)
            } ?: Result.failure(FirebaseAuthException("", ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            auth.currentUser?.let {
                val profileUpdate = userProfileChangeRequest {
                    displayName = mergeFullName(firstName, lastName)
                }
                it.updateProfile(profileUpdate).await()
                Result.success(it)
            } ?: Result.failure(FirebaseAuthException("", ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        googleSignInClient.signOut().await()
    }

    override suspend fun changePassword(newPassword: String): Result<FirebaseUser> {
        return try {
            auth.currentUser?.let {
                it.updatePassword(newPassword).await()
                Result.success(it)
            } ?: Result.failure(FirebaseAuthWeakPasswordException("", "", ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}