package com.scrumteam.mytask.data.mapper

import android.content.Context
import java.time.LocalDate
import java.time.LocalTime

fun String.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this)
    } catch (e: Exception) {
        if (this.substring(5) == "02-29")
            LocalDate.parse("${this.substring(0, 5)}03-91")
        else null
    }
}

fun String.toLocalTime(): LocalTime? {
    return try {
        LocalTime.parse(this)
    } catch (e: Exception) {
        null
    }
}
