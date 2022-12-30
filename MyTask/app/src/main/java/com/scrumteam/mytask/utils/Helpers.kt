package com.scrumteam.mytask.utils

import android.util.Patterns
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.scrumteam.mytask.R

object Helpers {
    fun String.isNotValidEmail() =
        this.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(this).matches()

    fun handlingException(exception: Throwable): Int {
        return when (exception) {
            is FirebaseAuthInvalidUserException -> R.string.text_message_error_auth_something_wrong
            is FirebaseAuthInvalidCredentialsException -> R.string.text_message_error_auth_something_wrong
            is FirebaseNetworkException -> R.string.text_message_error_internet_connection
            else -> R.string.text_message_error_something
        }
    }
}