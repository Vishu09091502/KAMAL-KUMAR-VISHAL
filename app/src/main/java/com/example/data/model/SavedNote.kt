package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity to store study notes locally in Room Database for offline access.
 * Contains cached document content, curriculum metadata, and local study notes.
 */
@Entity(tableName = "saved_notes")
data class SavedNote(
    @PrimaryKey
    val noteId: String,
    val title: String,
    val description: String,
    val subjectName: String,
    val subjectId: String,
    val branchCode: String,
    val branchId: String,
    val semesterNumber: Int,
    val fileType: String = "PDF",
    val fileSize: String = "3.5 MB",
    val fileName: String = "document.pdf",
    val filePath: String = "",
    val uploadDate: String = "",
    val tags: String = "",
    val content: String = "",
    val savedAt: Long = System.currentTimeMillis(),
    val offlineNoteText: String = ""
)
