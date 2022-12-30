package com.scrumteam.mytask.utils

import androidx.annotation.StringRes

sealed class UiText{
    data class DynamicString(val value: String) : UiText()
    data class StringResource(@StringRes val id: Int) : UiText()
}
