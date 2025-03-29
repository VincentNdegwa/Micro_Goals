package com.example.microgoals.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "users")
data class User(
    @PrimaryKey val userId: String,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val isCurrentUser: Boolean = false,
    val loggedInAt:LocalDateTime = LocalDateTime.now()
)

