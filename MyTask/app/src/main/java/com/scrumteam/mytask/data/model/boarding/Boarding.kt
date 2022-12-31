package com.scrumteam.mytask.data.model.boarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Boarding(
    @DrawableRes
    val iconRes: Int,
    @StringRes
    val titleRes: Int,
    @StringRes
    val descRes: Int
)
