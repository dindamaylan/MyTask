package com.scrumteam.mytask.utils

import android.util.Patterns

object Helpers {
    fun String.isNotValidEmail() =
        this.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(this).matches()
}