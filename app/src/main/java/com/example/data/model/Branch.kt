package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey
    val id: String,
    val name: String,
    val code: String,
    val iconName: String = "computer",
    val description: String = ""
)
