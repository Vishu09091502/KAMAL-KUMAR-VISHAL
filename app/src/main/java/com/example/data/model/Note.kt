package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId"), Index("branchId"), Index("semesterId")]
)
data class Note(
    @PrimaryKey
    val id: String,
    val subjectId: String,
    val semesterId: String,
    val branchId: String,
    val title: String,
    val description: String,
    val fileType: String = "PDF",
    val fileSize: String = "3.5 MB",
    val fileName: String = "document.pdf",
    val filePath: String = "",
    val uploadDate: String = "2026-09-12",
    val tags: String = "Diploma,Engineering,Notes",
    val downloadCount: Int = 0,
    val content: String = ""
)
