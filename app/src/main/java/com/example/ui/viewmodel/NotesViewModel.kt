package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NotesDatabase
import com.example.data.model.AdminUser
import com.example.data.model.Branch
import com.example.data.model.Note
import com.example.data.model.SavedNote
import com.example.data.model.Semester
import com.example.data.model.Subject
import com.example.data.repository.NotesRepository
import com.example.data.util.FileDownloadHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardStats(
    val totalBranches: Int = 0,
    val totalSemesters: Int = 0,
    val totalSubjects: Int = 0,
    val totalNotes: Int = 0,
    val totalDownloads: Int = 0
)

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotesRepository

    init {
        val db = NotesDatabase.getDatabase(application, viewModelScope)
        repository = NotesRepository(db.notesDao(), db.savedNotesDao())
        viewModelScope.launch {
            repository.ensureDataSeeded()
        }
    }

    // Browsing State
    val branches: StateFlow<List<Branch>> = repository.allBranches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSemesters: StateFlow<List<Semester>> = repository.allSemesters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubjects: StateFlow<List<Subject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<Note>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Offline Saved Notes State (Room Database)
    val savedNotes: StateFlow<List<SavedNote>> = repository.allSavedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedNotesCount: StateFlow<Int> = repository.savedNotesCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _selectedBranchId = MutableStateFlow<String?>("branch_cse")
    val selectedBranchId = _selectedBranchId.asStateFlow()

    private val _selectedSemesterNumber = MutableStateFlow<Int>(3)
    val selectedSemesterNumber = _selectedSemesterNumber.asStateFlow()

    private val _selectedSubjectId = MutableStateFlow<String?>("sub_cse_s3_dsa")
    val selectedSubjectId = _selectedSubjectId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<Note>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            repository.searchNotes(query.trim())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentNotes: StateFlow<List<Note>> = repository.getRecentNotes(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Session State
    private val _currentAdmin = MutableStateFlow<AdminUser?>(null)
    val currentAdmin: StateFlow<AdminUser?> = _currentAdmin.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    // Dashboard Stats combining counts
    val dashboardStats: StateFlow<DashboardStats> = combine(
        repository.branchCount,
        repository.semesterCount,
        repository.subjectCount,
        repository.noteCount,
        repository.totalDownloads
    ) { bCount, semCount, subCount, nCount, downloads ->
        DashboardStats(
            totalBranches = bCount,
            totalSemesters = semCount,
            totalSubjects = subCount,
            totalNotes = nCount,
            totalDownloads = downloads ?: 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    fun selectBranch(branchId: String) {
        _selectedBranchId.value = branchId
        // Automatically default semester to 3 or 1
        _selectedSemesterNumber.value = 3
        _selectedSubjectId.value = null
    }

    fun selectSemester(semesterNum: Int) {
        _selectedSemesterNumber.value = semesterNum
        _selectedSubjectId.value = null
    }

    fun selectSubject(subjectId: String?) {
        _selectedSubjectId.value = subjectId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    // --- Offline Saved Materials (Room Database) ---
    fun isNoteSaved(noteId: String): Flow<Boolean> = repository.isNoteSaved(noteId)

    fun toggleSaveForOffline(note: Note) {
        viewModelScope.launch {
            val isSaved = repository.isNoteSaved(note.id).firstOrNull() ?: false
            if (isSaved) {
                repository.removeSavedNote(note.id)
                _actionMessage.value = "Removed '${note.title}' from offline saved materials"
            } else {
                val subject = allSubjects.value.firstOrNull { it.id == note.subjectId }
                val branch = branches.value.firstOrNull { it.id == note.branchId }
                val semNumber = allSemesters.value.firstOrNull { it.id == note.semesterId }?.semesterNumber ?: 3

                repository.saveNoteForOffline(
                    note = note,
                    subjectName = subject?.name ?: "Diploma Subject",
                    branchCode = branch?.code ?: "ENGG",
                    semesterNumber = semNumber
                )
                _actionMessage.value = "Saved '${note.title}' to Room Database for offline access"
            }
        }
    }

    fun removeSavedNote(noteId: String) {
        viewModelScope.launch {
            repository.removeSavedNote(noteId)
            _actionMessage.value = "Note removed from offline storage"
        }
    }

    fun updateSavedNoteNotes(noteId: String, notesText: String) {
        viewModelScope.launch {
            repository.updateSavedNoteNotes(noteId, notesText)
            _actionMessage.value = "Study notes saved offline"
        }
    }

    // --- Admin Auth ---
    fun loginAdmin(username: String, passwordAttempt: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginError.value = null
            if (username.isBlank() || passwordAttempt.isBlank()) {
                _loginError.value = "Username and password cannot be empty"
                return@launch
            }
            val admin = repository.authenticateAdmin(username, passwordAttempt)
            if (admin != null) {
                _currentAdmin.value = admin
                _loginError.value = null
                onSuccess()
            } else {
                _loginError.value = "Invalid username or password. Default is admin / admin123"
            }
        }
    }

    fun logoutAdmin() {
        _currentAdmin.value = null
        _actionMessage.value = "Logged out successfully"
    }

    // --- Download Handler ---
    fun downloadNote(context: Context, note: Note) {
        viewModelScope.launch {
            val success = FileDownloadHelper.downloadNote(context, note)
            if (success) {
                repository.incrementDownload(note.id)
                // Also auto-cache to offline Room DB so user can access it anytime offline
                val subject = allSubjects.value.firstOrNull { it.id == note.subjectId }
                val branch = branches.value.firstOrNull { it.id == note.branchId }
                val semNumber = allSemesters.value.firstOrNull { it.id == note.semesterId }?.semesterNumber ?: 3

                repository.saveNoteForOffline(
                    note = note,
                    subjectName = subject?.name ?: "Diploma Subject",
                    branchCode = branch?.code ?: "ENGG",
                    semesterNumber = semNumber
                )

                _actionMessage.value = "Downloaded & saved to Room offline database!"
            }
        }
    }

    // --- Admin CRUD Operations ---
    fun addBranch(name: String, code: String, iconName: String, description: String) {
        viewModelScope.launch {
            val id = "branch_" + code.lowercase().replace(Regex("[^a-z0-9]"), "")
            val branch = Branch(id = id, name = name, code = code, iconName = iconName, description = description)
            repository.insertBranch(branch)
            // auto-create 6 semesters for this branch
            for (i in 1..6) {
                repository.insertSemester(
                    Semester(
                        id = "${id}_sem_$i",
                        branchId = id,
                        semesterNumber = i,
                        title = "Semester $i"
                    )
                )
            }
            _actionMessage.value = "Branch '$name' added with 6 semesters"
        }
    }

    fun updateBranch(branch: Branch) {
        viewModelScope.launch {
            repository.updateBranch(branch)
            _actionMessage.value = "Branch '${branch.name}' updated"
        }
    }

    fun deleteBranch(id: String) {
        viewModelScope.launch {
            repository.deleteBranch(id)
            if (_selectedBranchId.value == id) {
                _selectedBranchId.value = branches.value.firstOrNull { it.id != id }?.id
            }
            _actionMessage.value = "Branch removed"
        }
    }

    fun addSubject(branchId: String, semesterId: String, name: String, code: String, description: String) {
        viewModelScope.launch {
            val subId = "sub_" + System.currentTimeMillis().toString().takeLast(6)
            val subject = Subject(
                id = subId,
                semesterId = semesterId,
                branchId = branchId,
                name = name,
                code = code,
                description = description
            )
            repository.insertSubject(subject)
            _actionMessage.value = "Subject '$name' added"
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            repository.updateSubject(subject)
            _actionMessage.value = "Subject '${subject.name}' updated"
        }
    }

    fun deleteSubject(id: String) {
        viewModelScope.launch {
            repository.deleteSubject(id)
            _actionMessage.value = "Subject removed"
        }
    }

    fun addNote(
        subjectId: String,
        branchId: String,
        semesterId: String,
        title: String,
        description: String,
        fileType: String,
        fileSize: String,
        tags: String,
        content: String,
        fileName: String? = null
    ) {
        viewModelScope.launch {
            val extension = fileType.lowercase()
            val sanitizedName = title.take(20).replace(Regex("[^a-zA-Z0-9]"), "_")
            val resolvedFileName = if (!fileName.isNullOrBlank()) fileName.trim() else "${sanitizedName}_Notes.$extension"
            val note = Note(
                id = "note_" + System.currentTimeMillis(),
                subjectId = subjectId,
                branchId = branchId,
                semesterId = semesterId,
                title = title,
                description = description,
                fileType = fileType,
                fileSize = if (fileSize.isBlank()) "3.5 MB" else fileSize,
                fileName = resolvedFileName,
                filePath = "uploads/$resolvedFileName",
                uploadDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
                tags = tags,
                downloadCount = 0,
                content = content
            )
            repository.insertNote(note)
            _actionMessage.value = "Note '$title' uploaded successfully"
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
            _actionMessage.value = "Note '${note.title}' updated"
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            repository.deleteNote(id)
            _actionMessage.value = "Note deleted"
        }
    }

    fun getRelatedNotes(subjectId: String, currentNoteId: String) =
        repository.getRelatedNotes(subjectId, currentNoteId, 4)

    fun getNoteById(id: String) = repository.getNoteById(id)
}
