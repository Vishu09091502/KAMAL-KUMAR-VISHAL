package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.NotesDatabase
import com.example.data.model.SavedNote
import com.example.data.util.SecurityHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: NotesDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Polymate Edu Hub", appName)
    }

    @Test
    fun `verify admin password hashing and verification`() {
        val rawPassword = "admin123"
        val hash = SecurityHelper.hashPassword(rawPassword)

        assertTrue(SecurityHelper.verifyPassword("admin123", hash))
        assertFalse(SecurityHelper.verifyPassword("wrongpass", hash))
    }

    @Test
    fun `verify room saved notes entity and dao offline access`() = runBlocking {
        val dao = database.savedNotesDao()

        val sampleSaved = SavedNote(
            noteId = "test_offline_dsa",
            title = "Data Structures Complete Revision Notes",
            description = "Stack, Queue, Linked List and Trees notes for offline exam study",
            subjectName = "Data Structures",
            subjectId = "sub_cse_s3_dsa",
            branchCode = "CSE",
            branchId = "branch_cse",
            semesterNumber = 3,
            fileType = "PDF",
            fileSize = "2.8 MB",
            fileName = "DSA_Revision.pdf",
            filePath = "uploads/dsa.pdf",
            uploadDate = "2026-09-25",
            tags = "DSA,Offline,Exam",
            content = "Binary Trees Traversal: Inorder, Preorder, Postorder.",
            offlineNoteText = "Revise binary search algorithms."
        )

        // Test Insert
        dao.saveNote(sampleSaved)

        // Test isNoteSaved
        val isSaved = dao.isNoteSaved("test_offline_dsa").first()
        assertTrue(isSaved)

        // Test Query
        val allSaved = dao.getAllSavedNotes().first()
        assertEquals(1, allSaved.size)
        assertEquals("Data Structures Complete Revision Notes", allSaved[0].title)

        // Test Direct Get
        val retrieved = dao.getSavedNoteDirect("test_offline_dsa")
        assertNotNull(retrieved)
        assertEquals("Revise binary search algorithms.", retrieved?.offlineNoteText)

        // Test Delete / Remove
        dao.removeSavedNote("test_offline_dsa")
        val isStillSaved = dao.isNoteSaved("test_offline_dsa").first()
        assertFalse(isStillSaved)
    }

    @Test
    fun `verify prepopulated initial saved notes evaluation and repository seeding`() = runBlocking {
        // Ensure PrepopulatedData.initialSavedNotes evaluates without NoSuchElementException
        val initialNotes = com.example.data.local.PrepopulatedData.initialSavedNotes
        assertTrue("Initial saved notes should not be empty", initialNotes.isNotEmpty())
        assertEquals("note_dsa_trees_graphs", initialNotes.first().noteId)

        val repository = com.example.data.repository.NotesRepository(
            notesDao = database.notesDao(),
            savedNotesDao = database.savedNotesDao()
        )
        // Ensure ensureDataSeeded succeeds cleanly
        repository.ensureDataSeeded()

        val savedCount = database.savedNotesDao().getSavedNotesCount().first()
        assertTrue("Saved notes count should be at least 1 after seeding", savedCount >= 1)
    }

    @Test
    fun `verify attached document presets and custom fileName persistence`() = runBlocking {
        val presets = com.example.ui.screens.standardDocumentPresets
        assertTrue("Presets should have standard document options", presets.size >= 6)
        val labPreset = presets.first { it.id == "preset_lab_manual" }
        assertEquals("DOCX", labPreset.fileType)

        // Insert note with attached document format details
        val note = com.example.data.model.Note(
            id = "note_test_attached_lab",
            subjectId = "sub_cse_s3_dsa",
            semesterId = "branch_cse_sem_3",
            branchId = "branch_cse",
            title = "DSA Lab Practical Manual",
            description = labPreset.description,
            fileType = labPreset.fileType,
            fileSize = labPreset.defaultSize,
            fileName = labPreset.defaultFileName,
            filePath = "uploads/${labPreset.defaultFileName}",
            tags = labPreset.tags,
            content = labPreset.contentOutline
        )
        database.notesDao().insertNote(note)

        val retrievedNote = database.notesDao().getNoteById("note_test_attached_lab").first()
        assertNotNull(retrievedNote)
        assertEquals("Practical_Lab_Manual_Viva.docx", retrievedNote?.fileName)
        assertEquals("DOCX", retrievedNote?.fileType)
    }
}
