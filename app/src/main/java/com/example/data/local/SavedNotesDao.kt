package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SavedNote
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for local offline notes stored in Room database.
 */
@Dao
interface SavedNotesDao {

    @Query("SELECT * FROM saved_notes ORDER BY savedAt DESC")
    fun getAllSavedNotes(): Flow<List<SavedNote>>

    @Query("SELECT * FROM saved_notes WHERE noteId = :noteId LIMIT 1")
    fun getSavedNoteById(noteId: String): Flow<SavedNote?>

    @Query("SELECT * FROM saved_notes WHERE noteId = :noteId LIMIT 1")
    suspend fun getSavedNoteDirect(noteId: String): SavedNote?

    @Query("SELECT EXISTS(SELECT 1 FROM saved_notes WHERE noteId = :noteId)")
    fun isNoteSaved(noteId: String): Flow<Boolean>

    @Query("""
        SELECT * FROM saved_notes 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%'
           OR tags LIKE '%' || :query || '%'
           OR subjectName LIKE '%' || :query || '%'
        ORDER BY savedAt DESC
    """)
    fun searchSavedNotes(query: String): Flow<List<SavedNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(savedNote: SavedNote)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNotes(savedNotes: List<SavedNote>)

    @Update
    suspend fun updateSavedNote(savedNote: SavedNote)

    @Query("DELETE FROM saved_notes WHERE noteId = :noteId")
    suspend fun removeSavedNote(noteId: String)

    @Query("DELETE FROM saved_notes")
    suspend fun clearAllSavedNotes()

    @Query("SELECT COUNT(*) FROM saved_notes")
    fun getSavedNotesCount(): Flow<Int>
}
