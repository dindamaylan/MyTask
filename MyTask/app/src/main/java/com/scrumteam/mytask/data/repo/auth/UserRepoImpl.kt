package com.scrumteam.mytask.data.repo.auth

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.scrumteam.mytask.R
import com.scrumteam.mytask.data.model.User
import com.scrumteam.mytask.utils.Constants.USERS_COLLECTION
import com.scrumteam.mytask.utils.Helpers.handlingException
import com.scrumteam.mytask.utils.Result
import com.scrumteam.mytask.utils.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
    private val db: FirebaseFirestore,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UserRepo {

    override val currentUser: Flow<FirebaseUser> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            it.currentUser?.let { user ->
                trySend(user)
            }
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }.flowOn(ioDispatcher)

    override suspend fun checkUserIsExists(userId: String): Boolean {
        return db.collection(USERS_COLLECTION).document(userId).get().await().exists()
    }

    override fun getUser(userId: String): Flow<User> =
        db.collection(USERS_COLLECTION).document(userId).snapshots()
            .mapNotNull { it.toObject<User>() }.flowOn(ioDispatcher)


    override fun addUser(firebaseUser: FirebaseUser): Flow<Result<UiText>> =
        flow<Result<UiText>> {
            val userCollection = db.collection(USERS_COLLECTION)

            val userData = User(
                id = firebaseUser.uid,
                email = firebaseUser.email,
                photo = firebaseUser.photoUrl.toString(),
                first_name = firebaseUser.displayName,
                last_name = firebaseUser.displayName
            )

            userCollection.document(firebaseUser.uid).set(userData).await()

            emit(Result.Success(UiText.StringResource(R.string.text_result_register_success)))
        }.onStart { emit(Result.Loading) }
            .catch { exception ->
                emit(Result.Error(UiText.StringResource(handlingException(exception))))
            }
            .flowOn(ioDispatcher)


    override fun loginWithEmailPassword(email: String, password: String): Flow<Result<UiText>> =
        flow {
            val result = auth.signInWithEmailAndPassword(email, password).await()

            result.user?.let {

                emit(Result.Success(UiText.StringResource(R.string.text_result_login_success)))

            } ?: emit(Result.Error(UiText.StringResource(R.string.text_message_error_something)))
        }.onStart { emit(Result.Loading) }
            .catch { exception ->
                emit(
                    Result.Error(
                        UiText.StringResource(
                            handlingException(exception)
                        )
                    )
                )
            }
            .flowOn(ioDispatcher)

    override fun loginWithGoogle(credential: AuthCredential): Flow<Result<UiText>> = flow {
        val result = auth.signInWithCredential(credential).await()

        result.user?.let {

            emit(Result.Success(UiText.StringResource(R.string.text_result_register_success)))
        } ?: emit(Result.Error(UiText.StringResource(R.string.text_message_error_something)))
    }.onStart { emit(Result.Loading) }
        .catch { exception -> emit(Result.Error(UiText.StringResource(handlingException(exception)))) }
        .flowOn(ioDispatcher)


    override fun register(
        first_name: String,
        last_name: String,
        email: String,
        password: String
    ): Flow<Result<UiText>> {
        TODO("Not yet implemented")
    }

    override fun logout(): Flow<Result<UiText>> = flow<Result<UiText>> {
        auth.signOut()
        googleSignInClient.signOut().await()
        emit(Result.Success(UiText.StringResource(R.string.text_result_logout_success)))

    }.onStart { emit(Result.Loading) }
        .catch { exception ->
            emit(Result.Error(UiText.StringResource(handlingException(exception))))
        }
        .flowOn(ioDispatcher)

}