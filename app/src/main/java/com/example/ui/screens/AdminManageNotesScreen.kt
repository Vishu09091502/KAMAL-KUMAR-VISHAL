package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Note
import com.example.ui.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageNotesScreen(
    preselectedSubjectId: String? = null,
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val branches by viewModel.branches.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(if (preselectedSubjectId != null) 0 else 1) }

    // Upload Form State
    val initialSubject = allSubjects.firstOrNull { it.id == preselectedSubjectId }
    var selectedBranchId by remember { mutableStateOf(initialSubject?.branchId ?: branches.firstOrNull()?.id ?: "branch_cse") }
    var selectedSemNum by remember { mutableIntStateOf(3) }
    var selectedSubjectId by remember { mutableStateOf(preselectedSubjectId ?: allSubjects.firstOrNull { it.branchId == selectedBranchId }?.id ?: "") }

    var noteTitle by remember { mutableStateOf("") }
    var noteDescription by remember { mutableStateOf("") }
    var noteFileType by remember { mutableStateOf("PDF") }
    var noteFileSize by remember { mutableStateOf("3.8 MB") }
    var noteTags by remember { mutableStateOf("Diploma, Revision, Exam Capsule") }
    var noteContent by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }
    var uploadSuccess by remember { mutableStateOf(false) }

    // Edit/Delete Dialog states
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    val currentSemesterId = "${selectedBranchId}_sem_$selectedSemNum"
    val availableSubjects = allSubjects.filter { it.branchId == selectedBranchId && it.semesterId == currentSemesterId }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Study Notes Management", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back_from_manage_notes")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Upload New Note") },
                    icon = { Icon(imageVector = Icons.Default.FileUpload, contentDescription = "Upload") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Notes Catalog (${allNotes.size})") },
                    icon = { Icon(imageVector = Icons.Default.List, contentDescription = "Catalog") }
                )
            }

            if (selectedTab == 0) {
                // UPLOAD NEW NOTE FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Upload & Publish Study Document",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // 1. Branch Selector
                    var branchExp by remember { mutableStateOf(false) }
                    val currentBranch = branches.firstOrNull { it.id == selectedBranchId }
                    ExposedDropdownMenuBox(
                        expanded = branchExp,
                        onExpandedChange = { branchExp = !branchExp },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = "${currentBranch?.code ?: ""} - ${currentBranch?.name ?: "Select Branch"}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target Branch") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = branchExp,
                            onDismissRequest = { branchExp = false }
                        ) {
                            branches.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text("${b.code} - ${b.name}") },
                                    onClick = {
                                        selectedBranchId = b.id
                                        branchExp = false
                                        selectedSubjectId = allSubjects.firstOrNull { it.branchId == b.id }?.id ?: ""
                                    }
                                )
                            }
                        }
                    }

                    // 2. Semester Selector
                    var semExp by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = semExp,
                        onExpandedChange = { semExp = !semExp },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = "Semester $selectedSemNum",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target Semester") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semExp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = semExp,
                            onDismissRequest = { semExp = false }
                        ) {
                            (1..6).forEach { sem ->
                                DropdownMenuItem(
                                    text = { Text("Semester $sem") },
                                    onClick = {
                                        selectedSemNum = sem
                                        semExp = false
                                        val sId = "${selectedBranchId}_sem_$sem"
                                        selectedSubjectId = allSubjects.firstOrNull { it.branchId == selectedBranchId && it.semesterId == sId }?.id ?: ""
                                    }
                                )
                            }
                        }
                    }

                    // 3. Subject Selector
                    var subExp by remember { mutableStateOf(false) }
                    val currentSubject = availableSubjects.firstOrNull { it.id == selectedSubjectId }
                    ExposedDropdownMenuBox(
                        expanded = subExp,
                        onExpandedChange = { subExp = !subExp },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = if (currentSubject != null) "${currentSubject.code} - ${currentSubject.name}" else "No Subject in this Sem",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target Subject") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subExp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = subExp,
                            onDismissRequest = { subExp = false }
                        ) {
                            availableSubjects.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${s.code} - ${s.name}") },
                                    onClick = {
                                        selectedSubjectId = s.id
                                        subExp = false
                                    }
                                )
                            }
                        }
                    }

                    // 4. Note Title
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Document Title (e.g. Unit 1 & 2 Complete Lecture Notes)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_note_title")
                    )

                    // 5. Short Description
                    OutlinedTextField(
                        value = noteDescription,
                        onValueChange = { noteDescription = it },
                        label = { Text("Short Description / Summary") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    // 6. File Format & Size
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // File Type Dropdown
                        var typeExp by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = typeExp,
                            onExpandedChange = { typeExp = !typeExp },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = noteFileType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Format (PDF/DOCX/PPTX)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExp) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = typeExp,
                                onDismissRequest = { typeExp = false }
                            ) {
                                listOf("PDF", "DOCX", "PPTX").forEach { format ->
                                    DropdownMenuItem(
                                        text = { Text(format) },
                                        onClick = {
                                            noteFileType = format
                                            typeExp = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = noteFileSize,
                            onValueChange = { noteFileSize = it },
                            label = { Text("File Size (e.g. 4.2 MB)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // 7. Tags
                    OutlinedTextField(
                        value = noteTags,
                        onValueChange = { noteTags = it },
                        label = { Text("Tags (comma separated)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 8. Study Notes Content
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Document Content / Syllabus Outline (shown in preview & downloaded file)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = { Text("Enter detailed study topics, formulas, exam tips, and syllabus notes...") }
                    )

                    if (formError != null) {
                        Text(text = formError ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    if (uploadSuccess) {
                        Text(
                            text = "Note published successfully! Available for all students to download.",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Submit Button
                    Button(
                        onClick = {
                            if (selectedSubjectId.isBlank()) {
                                formError = "Please select a valid subject"
                            } else if (noteTitle.isBlank()) {
                                formError = "Title cannot be empty"
                            } else if (noteDescription.isBlank()) {
                                formError = "Description cannot be empty"
                            } else {
                                formError = null
                                viewModel.addNote(
                                    subjectId = selectedSubjectId,
                                    branchId = selectedBranchId,
                                    semesterId = currentSemesterId,
                                    title = noteTitle.trim(),
                                    description = noteDescription.trim(),
                                    fileType = noteFileType,
                                    fileSize = noteFileSize.trim(),
                                    tags = noteTags.trim(),
                                    content = noteContent.ifBlank { "Study notes and revision formulas for diploma engineering students." }
                                )
                                uploadSuccess = true
                                noteTitle = ""
                                noteDescription = ""
                                noteContent = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_submit_upload_note"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FileUpload, contentDescription = "Upload")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish Note for Students", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                // ALL NOTES CATALOG FOR EDITING & DELETION
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allNotes, key = { it.id }) { note ->
                        val subject = allSubjects.firstOrNull { it.id == note.subjectId }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${subject?.name ?: note.subjectId} • ${note.fileType.uppercase()} • ${note.downloadCount} downloads",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    IconButton(onClick = { noteToEdit = note }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit note",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(onClick = { noteToDelete = note }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete note",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- EDIT NOTE DIALOG ---
    if (noteToEdit != null) {
        val note = noteToEdit!!
        var editTitle by remember { mutableStateOf(note.title) }
        var editDesc by remember { mutableStateOf(note.description) }
        var editTags by remember { mutableStateOf(note.tags) }
        var editFormat by remember { mutableStateOf(note.fileType) }

        AlertDialog(
            onDismissRequest = { noteToEdit = null },
            title = { Text("Edit Note Metadata") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editTags,
                        onValueChange = { editTags = it },
                        label = { Text("Tags") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateNote(
                            note.copy(
                                title = editTitle.trim(),
                                description = editDesc.trim(),
                                tags = editTags.trim(),
                                fileType = editFormat
                            )
                        )
                        noteToEdit = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { noteToEdit = null }) { Text("Cancel") }
            }
        )
    }

    // --- DELETE NOTE DIALOG ---
    if (noteToDelete != null) {
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Delete '${noteToDelete?.title}'?") },
            text = { Text("Are you sure you want to permanently delete this study note?") },
            confirmButton = {
                Button(
                    onClick = {
                        noteToDelete?.let { viewModel.deleteNote(it.id) }
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { noteToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
