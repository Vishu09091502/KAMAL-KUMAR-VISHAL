package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminUser
import com.example.data.model.Branch
import com.example.data.model.Note
import com.example.data.model.Semester
import com.example.data.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    // --- BRANCHES ---
    @Query("SELECT * FROM branches ORDER BY name ASC")
    fun getAllBranches(): Flow<List<Branch>>

    @Query("SELECT * FROM branches WHERE id = :id")
    fun getBranchById(id: String): Flow<Branch?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branches: List<Branch>)

    @Update
    suspend fun updateBranch(branch: Branch)

    @Query("DELETE FROM branches WHERE id = :id")
    suspend fun deleteBranchById(id: String)

    @Query("SELECT COUNT(*) FROM branches")
    fun getBranchCount(): Flow<Int>

    // --- SEMESTERS ---
    @Query("SELECT * FROM semesters ORDER BY semesterNumber ASC")
    fun getAllSemesters(): Flow<List<Semester>>

    @Query("SELECT * FROM semesters WHERE branchId = :branchId ORDER BY semesterNumber ASC")
    fun getSemestersForBranch(branchId: String): Flow<List<Semester>>

    @Query("SELECT * FROM semesters WHERE id = :id")
    fun getSemesterById(id: String): Flow<Semester?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemester(semester: Semester)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesters(semesters: List<Semester>)

    @Update
    suspend fun updateSemester(semester: Semester)

    @Query("DELETE FROM semesters WHERE id = :id")
    suspend fun deleteSemesterById(id: String)

    @Query("SELECT COUNT(*) FROM semesters")
    fun getSemesterCount(): Flow<Int>

    // --- SUBJECTS ---
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE semesterId = :semesterId ORDER BY name ASC")
    fun getSubjectsForSemester(semesterId: String): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE branchId = :branchId ORDER BY name ASC")
    fun getSubjectsForBranch(branchId: String): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    fun getSubjectById(id: String): Flow<Subject?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: String)

    @Query("SELECT COUNT(*) FROM subjects")
    fun getSubjectCount(): Flow<Int>

    // --- NOTES ---
    @Query("SELECT * FROM notes ORDER BY uploadDate DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE subjectId = :subjectId ORDER BY uploadDate DESC")
    fun getNotesForSubject(subjectId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: String): Flow<Note?>

    @Query("""
        SELECT * FROM notes 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR tags LIKE '%' || :query || '%'
        ORDER BY uploadDate DESC
    """)
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes ORDER BY uploadDate DESC LIMIT :limit")
    fun getRecentNotes(limit: Int = 10): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE subjectId = :subjectId AND id != :currentNoteId LIMIT :limit")
    fun getRelatedNotes(subjectId: String, currentNoteId: String, limit: Int = 4): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<Note>)

    @Update
    suspend fun updateNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    @Query("UPDATE notes SET downloadCount = downloadCount + 1 WHERE id = :noteId")
    suspend fun incrementDownloadCount(noteId: String)

    @Query("SELECT COUNT(*) FROM notes")
    fun getNoteCount(): Flow<Int>

    @Query("SELECT SUM(downloadCount) FROM notes")
    fun getTotalDownloads(): Flow<Int?>

    // --- ADMIN USERS ---
    @Query("SELECT * FROM admin_users WHERE username = :username LIMIT 1")
    suspend fun getAdminUser(username: String): AdminUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminUser)

    @Query("SELECT * FROM admin_users")
    fun getAllAdmins(): Flow<List<AdminUser>>
}
