package com.scrumteam.mytask.data.model

import java.sql.Timestamp

data class User (
    val id: String? = null,
    val email: String? = null,
    val photo: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val createdAt: String = Timestamp(System.currentTimeMillis()).toString()
)