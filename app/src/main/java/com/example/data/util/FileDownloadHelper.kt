package com.example.data.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.Note
import java.io.File
import java.io.FileOutputStream

object FileDownloadHelper {

    fun getMimeType(fileType: String): String {
        return when (fileType.uppercase()) {
            "PDF" -> "application/pdf"
            "DOC", "DOCX" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "PPT", "PPTX" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
            else -> "text/plain"
        }
    }

    fun downloadNote(context: Context, note: Note): Boolean {
        try {
            val extension = when (note.fileType.uppercase()) {
                "DOC", "DOCX" -> ".docx"
                "PPT", "PPTX" -> ".pptx"
                "PDF" -> ".pdf"
                else -> ".txt"
            }
            val sanitizedTitle = note.title.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val fileName = "DiplomaNotes_${sanitizedTitle}_v1$extension"
            val mimeType = getMimeType(note.fileType)

            val documentContent = buildString {
                append("==================================================\n")
                append("       DIPLOMA ENGINEERING STUDY NOTES PORTAL      \n")
                append("==================================================\n")
                append("TITLE       : ${note.title}\n")
                append("BRANCH ID   : ${note.branchId}\n")
                append("SEMESTER ID : ${note.semesterId}\n")
                append("SUBJECT ID  : ${note.subjectId}\n")
                append("UPLOAD DATE : ${note.uploadDate}\n")
                append("TAGS        : ${note.tags}\n")
                append("FORMAT      : ${note.fileType}\n")
                append("SIZE        : ${note.fileSize}\n")
                append("==================================================\n\n")
                append("OVERVIEW & DESCRIPTION:\n")
                append(note.description)
                append("\n\n")
                append("--------------------------------------------------\n")
                append("DETAILED SYLLABUS & COMPREHENSIVE REVISION MODULE:\n")
                append("--------------------------------------------------\n")
                append(note.content.ifBlank { "Comprehensive curriculum-aligned study module prepared for diploma examinations." })
                append("\n\n==================================================\n")
                append("Downloaded via Diploma Notes Android Portal\n")
                append("Single-Click Verified Document\n")
                append("==================================================\n")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/DiplomaNotes")
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(documentContent.toByteArray(Charsets.UTF_8))
                    }
                    Toast.makeText(context, "Saved to Downloads/DiplomaNotes/$fileName", Toast.LENGTH_LONG).show()
                    return true
                }
            }

            // Fallback for older APIs or local file directory
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.filesDir
            val destFile = File(downloadsDir, fileName)
            FileOutputStream(destFile).use { fos ->
                fos.write(documentContent.toByteArray(Charsets.UTF_8))
            }

            Toast.makeText(context, "Downloaded: $fileName", Toast.LENGTH_LONG).show()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Download error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun shareNoteContent(context: Context, note: Note) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Diploma Study Note: ${note.title}")
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this Diploma Study Note:\n\n*${note.title}*\n${note.description}\n\nTags: ${note.tags}\nFile: ${note.fileName} (${note.fileSize})\n\n-- Shared from Diploma Notes Portal"
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Note"))
    }
}
