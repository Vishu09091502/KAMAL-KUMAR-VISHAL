package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Note
import com.example.ui.screens.AdminAuthScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminManageContentScreen
import com.example.ui.screens.AdminManageNotesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NoteDetailScreen
import com.example.ui.screens.SetupGuideDialog
import com.example.ui.screens.SubjectNotesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NotesViewModel

sealed interface Screen {
    data object Home : Screen
    data class SubjectNotes(val subjectId: String, val subjectName: String) : Screen
    data class NoteDetail(val noteId: String, val previousScreen: Screen = Home) : Screen
    data object AdminAuth : Screen
    data object AdminDashboard : Screen
    data class AdminManageContent(val initialTab: Int = 0) : Screen
    data class AdminManageNotes(val preselectedSubjectId: String? = null) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DiplomaNotesApp()
            }
        }
    }
}

@Composable
fun DiplomaNotesApp(
    viewModel: NotesViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showSetupGuide by remember { mutableStateOf(false) }
    val currentAdmin by viewModel.currentAdmin.collectAsStateWithLifecycle()

    // Global Back Handler
    BackHandler(enabled = currentScreen != Screen.Home) {
        currentScreen = when (val screen = currentScreen) {
            is Screen.SubjectNotes -> Screen.Home
            is Screen.NoteDetail -> screen.previousScreen
            is Screen.AdminAuth -> Screen.Home
            is Screen.AdminDashboard -> Screen.Home
            is Screen.AdminManageContent -> Screen.AdminDashboard
            is Screen.AdminManageNotes -> Screen.AdminDashboard
            Screen.Home -> Screen.Home
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToSubject = { subjectId, subjectName ->
                        currentScreen = Screen.SubjectNotes(subjectId, subjectName)
                    },
                    onNavigateToNoteDetail = { noteId ->
                        currentScreen = Screen.NoteDetail(noteId, Screen.Home)
                    },
                    onNavigateToAdmin = {
                        currentScreen = if (currentAdmin != null) {
                            Screen.AdminDashboard
                        } else {
                            Screen.AdminAuth
                        }
                    },
                    onOpenSetupGuide = {
                        showSetupGuide = true
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is Screen.SubjectNotes -> {
                SubjectNotesScreen(
                    subjectId = screen.subjectId,
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = Screen.Home },
                    onNavigateToNoteDetail = { noteId ->
                        currentScreen = Screen.NoteDetail(noteId, screen)
                    },
                    onNavigateToUploadNote = { subjectId ->
                        currentScreen = Screen.AdminManageNotes(preselectedSubjectId = subjectId)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is Screen.NoteDetail -> {
                NoteDetailScreen(
                    noteId = screen.noteId,
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = screen.previousScreen },
                    onNavigateToNoteDetail = { noteId ->
                        currentScreen = Screen.NoteDetail(noteId, screen.previousScreen)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is Screen.AdminAuth -> {
                AdminAuthScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = Screen.Home },
                    onLoginSuccess = { currentScreen = Screen.AdminDashboard },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is Screen.AdminDashboard -> {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = Screen.Home },
                    onNavigateToUploadNote = { currentScreen = Screen.AdminManageNotes() },
                    onNavigateToManageContent = { initialTab ->
                        currentScreen = Screen.AdminManageContent(initialTab)
                    },
                    onNavigateToManageNotes = { currentScreen = Screen.AdminManageNotes() },
                    onEditNote = { note ->
                        currentScreen = Screen.AdminManageNotes()
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is Screen.AdminManageContent -> {
                AdminManageContentScreen(
                    initialTab = screen.initialTab,
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = Screen.AdminDashboard },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is Screen.AdminManageNotes -> {
                AdminManageNotesScreen(
                    preselectedSubjectId = screen.preselectedSubjectId,
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = Screen.AdminDashboard },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    if (showSetupGuide) {
        SetupGuideDialog(onDismiss = { showSetupGuide = false })
    }
}

