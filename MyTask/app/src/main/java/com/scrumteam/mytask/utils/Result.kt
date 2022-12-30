package com.scrumteam.mytask.utils

sealed class Result<out R>{
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val uiText: UiText) : Result<Nothing>()
    object Loading : Result<Nothing>()
}