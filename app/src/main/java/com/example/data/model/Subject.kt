package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects",
    foreignKeys = [
        ForeignKey(
            entity = Semester::class,
            parentColumns = ["id"],
            childColumns = ["semesterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("semesterId"), Index("branchId")]
)
data class Subject(
    @PrimaryKey
    val id: String,
    val semesterId: String,
    val branchId: String,
    val name: String,
    val code: String,
    val description: String = ""
)
