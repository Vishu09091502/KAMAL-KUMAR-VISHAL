package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InsertDriveFile
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Note
import com.example.ui.viewmodel.NotesViewModel

data class AttachedDocumentPreset(
    val id: String,
    val name: String,
    val fileType: String,
    val defaultSize: String,
    val defaultFileName: String,
    val titlePattern: String,
    val description: String,
    val tags: String,
    val contentOutline: String
)

val standardDocumentPresets = listOf(
    AttachedDocumentPreset(
        id = "custom_attachment",
        name = "Custom Document (Upload New / Attach File)",
        fileType = "PDF",
        defaultSize = "3.5 MB",
        defaultFileName = "document.pdf",
        titlePattern = "",
        description = "Study notes and revision formulas for diploma engineering students.",
        tags = "Diploma, Notes, Revision",
        contentOutline = "Study notes and syllabus topics for semester examination."
    ),
    AttachedDocumentPreset(
        id = "preset_lecture_notes",
        name = "Lecture Notes & Unit Modules (PDF)",
        fileType = "PDF",
        defaultSize = "3.8 MB",
        defaultFileName = "Lecture_Notes_Modules.pdf",
        titlePattern = "Complete Lecture Notes & Chapter Modules",
        description = "Unit-by-unit comprehensive lecture notes, key derivations, solved illustrations, and exercise sets.",
        tags = "LectureNotes, UnitModule, Theory, SolvedQuestions, Diploma",
        contentOutline = "UNIT 1: Core Fundamentals, Definitions & Principles.\nUNIT 2: Detailed Mathematical Derivations & Diagrams.\nUNIT 3: Real-World Applications & Practice Problems.\nREVIEW: Key Questions and Summary."
    ),
    AttachedDocumentPreset(
        id = "preset_question_bank",
        name = "Previous Years Solved Question Bank (PDF)",
        fileType = "PDF",
        defaultSize = "4.6 MB",
        defaultFileName = "Solved_Question_Bank_PYQ.pdf",
        titlePattern = "Previous 5-Years Solved Board Question Papers",
        description = "Consolidated previous year polytechnic examination question bank with step-by-step marking schemes and model solutions.",
        tags = "PYQ, QuestionBank, BoardExam, SolvedPapers, Blueprint",
        contentOutline = "SECTION A: 2-Mark Short Answer Questions & Definitions.\nSECTION B: 5-Mark Conceptual Derivations & Explanations.\nSECTION C: 10-Mark Detailed Numerical Solutions & Schematics.\nBLUEPRINT: Chapter-wise mark distribution."
    ),
    AttachedDocumentPreset(
        id = "preset_lab_manual",
        name = "Practical Laboratory Manual & Viva Guide (DOCX)",
        fileType = "DOCX",
        defaultSize = "2.9 MB",
        defaultFileName = "Practical_Lab_Manual_Viva.docx",
        titlePattern = "Laboratory Practical Manual & Viva-Voce Guide",
        description = "Official laboratory practical instructions, hardware/software procedures, calculation tables, circuit schematics, and viva Q&A.",
        tags = "LabManual, Practical, VivaVoce, Experiments, Code",
        contentOutline = "EXPERIMENT 1: Apparatus, Theory, Setup & Results.\nEXPERIMENT 2: Step-by-Step Procedure, Observation Table & Graph.\nVIVA VOCE: 25 Frequently Asked Questions with Model Answers."
    ),
    AttachedDocumentPreset(
        id = "preset_formula_sheet",
        name = "Formula Cheat Sheet & Quick Revision Capsule (PDF)",
        fileType = "PDF",
        defaultSize = "1.8 MB",
        defaultFileName = "Formula_Sheet_Exam_Capsule.pdf",
        titlePattern = "Quick Formula Sheet & Exam Revision Capsule",
        description = "One-stop revision capsule containing all vital mathematical formulas, laws, SI units, and shortcut methods for fast revision.",
        tags = "FormulaSheet, RevisionCapsule, FastRevision, Summary, CheatSheet",
        contentOutline = "CORE FORMULAS: Units, Constants & Governing Laws.\nMNEMONICS: Quick recall techniques for exam night.\nSUMMARY TABLES: Comparison charts and recurring test formulas."
    ),
    AttachedDocumentPreset(
        id = "preset_seminar_slides",
        name = "Technical Seminar & Presentation Slides (PPTX)",
        fileType = "PPTX",
        defaultSize = "5.5 MB",
        defaultFileName = "Technical_Seminar_Presentation.pptx",
        titlePattern = "Seminar Presentation Slides & Technical Report",
        description = "Complete presentation slide deck covering system design, block diagrams, workflow methodology, and concluding project remarks.",
        tags = "Seminar, Presentation, PPTX, SlideDeck, ProjectReport",
        contentOutline = "SLIDE 1-3: Introduction, Problem Statement & Objectives.\nSLIDE 4-8: System Architecture, Circuit/Flowchart & Methodology.\nSLIDE 9-12: Findings, Advantages, Limitations & Future Scope."
    ),
    AttachedDocumentPreset(
        id = "preset_syllabus_scheme",
        name = "Curriculum Syllabus Scheme & Blueprint (PDF)",
        fileType = "PDF",
        defaultSize = "1.2 MB",
        defaultFileName = "Curriculum_Syllabus_Scheme.pdf",
        titlePattern = "Official Board Syllabus Copy & Exam Scheme",
        description = "State technical education board prescribed syllabus copy, teaching and examination scheme, course outcomes, and reference books.",
        tags = "Syllabus, Curriculum, CourseScheme, Blueprint, TextbookList",
        contentOutline = "TEACHING SCHEME: Lecture, Practical, and Credit distribution.\nEXAMINATION SCHEME: Theory, Practical, Internal, and External weightage.\nCOURSE OUTCOMES: Mapped syllabus topics and reference bibliography."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageNotesScreen(
    preselectedSubjectId: String? = null,
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val branches by viewModel.branches.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(if (preselectedSubjectId != null) 0 else 1) }

    // Upload Form State
    val initialSubject = allSubjects.firstOrNull { it.id == preselectedSubjectId }
    var selectedBranchId by remember { mutableStateOf(initialSubject?.branchId ?: branches.firstOrNull()?.id ?: "branch_cse") }
    var selectedSemNum by remember { mutableIntStateOf(3) }
    var selectedSubjectId by remember { mutableStateOf(preselectedSubjectId ?: allSubjects.firstOrNull { it.branchId == selectedBranchId }?.id ?: "") }

    // Attached Document Format Selector State
    var selectedDocPresetId by remember { mutableStateOf("custom_attachment") }
    var attachedFileName by remember { mutableStateOf("document.pdf") }

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
    val currentSubject = availableSubjects.firstOrNull { it.id == selectedSubjectId }

    // Document File Picker from Device Storage
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var name = "attached_document.pdf"
            var size = "3.2 MB"
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIndex != -1) {
                            cursor.getString(nameIndex)?.let { name = it }
                        }
                        if (sizeIndex != -1) {
                            val bytes = cursor.getLong(sizeIndex)
                            if (bytes > 0) {
                                size = String.format(java.util.Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0))
                            }
                        }
                    }
                }
            } catch (_: Exception) {}

            attachedFileName = name
            noteFileSize = size
            val ext = name.substringAfterLast('.', "").uppercase()
            if (ext in listOf("PDF", "DOCX", "DOC", "PPTX", "PPT")) {
                noteFileType = if (ext.startsWith("DOC")) "DOCX" else if (ext.startsWith("PPT")) "PPTX" else "PDF"
            }
            if (noteTitle.isBlank()) {
                val baseName = name.substringBeforeLast('.').replace('_', ' ')
                noteTitle = baseName
            }
        }
    }

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
                            label = { Text("1. Target Branch") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_target_branch")
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
                            label = { Text("2. Target Semester") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semExp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_target_semester")
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
                    ExposedDropdownMenuBox(
                        expanded = subExp,
                        onExpandedChange = { subExp = !subExp },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = if (currentSubject != null) "${currentSubject.code} - ${currentSubject.name}" else "No Subject in this Sem",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("3. Target Subject") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subExp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_target_subject")
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

                    // 4. ATTACHED DOCUMENT / EXISTING DOCUMENT FORMAT SELECTOR (Same format as Branch, Semester, Subject)
                    var docFormatExp by remember { mutableStateOf(false) }
                    val selectedPreset = standardDocumentPresets.firstOrNull { it.id == selectedDocPresetId }
                    val selectedExistingNote = allNotes.firstOrNull { "existing_${it.id}" == selectedDocPresetId }
                    val displayFormatLabel = selectedExistingNote?.let { "Attached Doc: ${it.title} (${it.fileType})" }
                        ?: (selectedPreset?.name ?: "Select Document Format / Preset")

                    ExposedDropdownMenuBox(
                        expanded = docFormatExp,
                        onExpandedChange = { docFormatExp = !docFormatExp },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = displayFormatLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("4. Attached Document / Document Format Preset") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = docFormatExp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("dropdown_attached_doc_format")
                        )
                        ExposedDropdownMenu(
                            expanded = docFormatExp,
                            onDismissRequest = { docFormatExp = false }
                        ) {
                            Text(
                                text = "STANDARD ATTACHED DOCUMENT FORMATS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )

                            standardDocumentPresets.forEach { preset ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(preset.name, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                text = "${preset.fileType} • ${preset.defaultSize} • ${preset.defaultFileName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedDocPresetId = preset.id
                                        noteFileType = preset.fileType
                                        noteFileSize = preset.defaultSize
                                        attachedFileName = preset.defaultFileName
                                        if (preset.id != "custom_attachment") {
                                            val subName = currentSubject?.name ?: "Diploma Course"
                                            noteTitle = "$subName - ${preset.titlePattern}"
                                            noteDescription = preset.description
                                            noteTags = preset.tags
                                            noteContent = preset.contentOutline
                                        }
                                        docFormatExp = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (preset.id == "custom_attachment") Icons.Default.FileUpload else Icons.Default.InsertDriveFile,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )
                            }

                            if (allNotes.isNotEmpty()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Text(
                                    text = "ALREADY ATTACHED DOCUMENTS IN HUB",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )

                                allNotes.take(8).forEach { existingNote ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = existingNote.title,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${existingNote.fileType} • ${existingNote.fileName} • ${existingNote.fileSize}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedDocPresetId = "existing_${existingNote.id}"
                                            noteTitle = "${existingNote.title} (Copy)"
                                            noteDescription = existingNote.description
                                            noteFileType = existingNote.fileType
                                            noteFileSize = existingNote.fileSize
                                            attachedFileName = existingNote.fileName
                                            noteTags = existingNote.tags
                                            noteContent = existingNote.content
                                            docFormatExp = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.AttachFile,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // ATTACHED DOCUMENT FILE STATUS & ATTACHMENT ACTION
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AttachFile,
                                        contentDescription = "Attachment",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Attached Document File",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Ready to Publish",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = attachedFileName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Format: $noteFileType  •  Size: $noteFileSize",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                OutlinedButton(
                                    onClick = { filePickerLauncher.launch("*/*") },
                                    modifier = Modifier.testTag("btn_attach_device_file"),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.FolderOpen, contentDescription = "Pick file", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Attach File", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    // 5. Note Title
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("5. Document Title (e.g. Unit 1 & 2 Complete Lecture Notes)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_note_title")
                    )

                    // 6. Short Description
                    OutlinedTextField(
                        value = noteDescription,
                        onValueChange = { noteDescription = it },
                        label = { Text("6. Short Description / Summary") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    // 7. File Format & Size
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

                    // 8. Tags
                    OutlinedTextField(
                        value = noteTags,
                        onValueChange = { noteTags = it },
                        label = { Text("7. Tags (comma separated)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 9. Study Notes Content
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("8. Document Content / Syllabus Outline (shown in preview & downloaded file)") },
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
                                    content = noteContent.ifBlank { "Study notes and revision formulas for diploma engineering students." },
                                    fileName = attachedFileName
                                )
                                uploadSuccess = true
                                noteTitle = ""
                                noteDescription = ""
                                noteContent = ""
                                selectedDocPresetId = "custom_attachment"
                                attachedFileName = "document.pdf"
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
                                        text = "${subject?.name ?: note.subjectId} • ${note.fileType.uppercase()} • ${note.fileName} • ${note.downloadCount} downloads",
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
