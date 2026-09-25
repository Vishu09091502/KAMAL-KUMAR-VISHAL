package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_users")
data class AdminUser(
    @PrimaryKey
    val username: String,
    val passwordHash: String,
    val displayName: String = "Administrator",
    val role: String = "SuperAdmin"
)
