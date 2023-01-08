package com.scrumteam.mytask.data.mapper

import com.scrumteam.mytask.utils.Constants.DATE_FORMATTER
import java.time.*
import java.time.format.DateTimeFormatter

fun LocalDateTime.toInSecond(): Long {
    return this.toEpochSecond(ZoneOffset.UTC)
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneOffset.UTC)
}


fun getLocalDateFormat(format: String = DATE_FORMATTER, localDateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern(format)
    return formatter.format(localDateTime)
}