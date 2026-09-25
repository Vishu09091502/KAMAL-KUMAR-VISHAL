package com.example.data.repository

import com.example.data.local.NotesDao
import com.example.data.local.PrepopulatedData
import com.example.data.model.AdminUser
import com.example.data.model.Branch
import com.example.data.model.Note
import com.example.data.model.Semester
import com.example.data.model.Subject
import com.example.data.util.SecurityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class NotesRepository(private val notesDao: NotesDao) {

    // Pre-check & auto seed if needed (e.g. on fresh migration)
    suspend fun ensureDataSeeded() = withContext(Dispatchers.IO) {
        val branchCount = notesDao.getBranchCount().firstOrNull() ?: 0
        if (branchCount == 0) {
            notesDao.insertAdmin(PrepopulatedData.defaultAdmin)
            notesDao.insertBranches(PrepopulatedData.branches)
            notesDao.insertSemesters(PrepopulatedData.generateSemesters())
            notesDao.insertSubjects(PrepopulatedData.subjects)
            notesDao.insertNotes(PrepopulatedData.sampleNotes)
        }
    }

    // Branches
    val allBranches: Flow<List<Branch>> = notesDao.getAllBranches()
    fun getBranchById(id: String): Flow<Branch?> = notesDao.getBranchById(id)
    suspend fun insertBranch(branch: Branch) = notesDao.insertBranch(branch)
    suspend fun updateBranch(branch: Branch) = notesDao.updateBranch(branch)
    suspend fun deleteBranch(id: String) = notesDao.deleteBranchById(id)
    val branchCount: Flow<Int> = notesDao.getBranchCount()

    // Semesters
    val allSemesters: Flow<List<Semester>> = notesDao.getAllSemesters()
    fun getSemestersForBranch(branchId: String): Flow<List<Semester>> = notesDao.getSemestersForBranch(branchId)
    fun getSemesterById(id: String): Flow<Semester?> = notesDao.getSemesterById(id)
    suspend fun insertSemester(semester: Semester) = notesDao.insertSemester(semester)
    suspend fun updateSemester(semester: Semester) = notesDao.updateSemester(semester)
    suspend fun deleteSemester(id: String) = notesDao.deleteSemesterById(id)
    val semesterCount: Flow<Int> = notesDao.getSemesterCount()

    // Subjects
    val allSubjects: Flow<List<Subject>> = notesDao.getAllSubjects()
    fun getSubjectsForSemester(semesterId: String): Flow<List<Subject>> = notesDao.getSubjectsForSemester(semesterId)
    fun getSubjectsForBranch(branchId: String): Flow<List<Subject>> = notesDao.getSubjectsForBranch(branchId)
    fun getSubjectById(id: String): Flow<Subject?> = notesDao.getSubjectById(id)
    suspend fun insertSubject(subject: Subject) = notesDao.insertSubject(subject)
    suspend fun updateSubject(subject: Subject) = notesDao.updateSubject(subject)
    suspend fun deleteSubject(id: String) = notesDao.deleteSubjectById(id)
    val subjectCount: Flow<Int> = notesDao.getSubjectCount()

    // Notes
    val allNotes: Flow<List<Note>> = notesDao.getAllNotes()
    fun getNotesForSubject(subjectId: String): Flow<List<Note>> = notesDao.getNotesForSubject(subjectId)
    fun getNoteById(id: String): Flow<Note?> = notesDao.getNoteById(id)
    fun searchNotes(query: String): Flow<List<Note>> = notesDao.searchNotes(query)
    fun getRecentNotes(limit: Int = 10): Flow<List<Note>> = notesDao.getRecentNotes(limit)
    fun getRelatedNotes(subjectId: String, currentNoteId: String, limit: Int = 4): Flow<List<Note>> =
        notesDao.getRelatedNotes(subjectId, currentNoteId, limit)

    suspend fun insertNote(note: Note) = notesDao.insertNote(note)
    suspend fun updateNote(note: Note) = notesDao.updateNote(note)
    suspend fun deleteNote(id: String) = notesDao.deleteNoteById(id)
    suspend fun incrementDownload(noteId: String) = notesDao.incrementDownloadCount(noteId)
    val noteCount: Flow<Int> = notesDao.getNoteCount()
    val totalDownloads: Flow<Int?> = notesDao.getTotalDownloads()

    // Authentication
    suspend fun authenticateAdmin(username: String, passwordAttempt: String): AdminUser? = withContext(Dispatchers.IO) {
        val admin = notesDao.getAdminUser(username.trim()) ?: return@withContext null
        if (SecurityHelper.verifyPassword(passwordAttempt, admin.passwordHash)) {
            admin
        } else {
            null
        }
    }

    suspend fun updateAdminPassword(username: String, newPlainPassword: String) = withContext(Dispatchers.IO) {
        val existing = notesDao.getAdminUser(username)
        if (existing != null) {
            val updated = existing.copy(passwordHash = SecurityHelper.hashPassword(newPlainPassword))
            notesDao.insertAdmin(updated)
        }
    }
}
