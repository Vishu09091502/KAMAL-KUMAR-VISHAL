package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "semesters",
    foreignKeys = [
        ForeignKey(
            entity = Branch::class,
            parentColumns = ["id"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("branchId")]
)
data class Semester(
    @PrimaryKey
    val id: String,
    val branchId: String,
    val semesterNumber: Int,
    val title: String = "Semester $semesterNumber"
)
