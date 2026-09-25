package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Branch
import com.example.data.model.Subject
import com.example.ui.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageContentScreen(
    initialTab: Int = 0,
    viewModel: NotesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(initialTab.coerceIn(0, 1)) }
    val branches by viewModel.branches.collectAsStateWithLifecycle()
    val allSubjects by viewModel.allSubjects.collectAsStateWithLifecycle()

    // Dialog States
    var showAddBranchDialog by remember { mutableStateOf(false) }
    var branchToEdit by remember { mutableStateOf<Branch?>(null) }
    var branchToDelete by remember { mutableStateOf<Branch?>(null) }

    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

    // Subject Filter selections
    var filterBranchId by remember { mutableStateOf(branches.firstOrNull()?.id ?: "branch_cse") }
    var filterSemesterNum by remember { mutableIntStateOf(3) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Hierarchy Management", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("btn_back_from_manage_content")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showAddBranchDialog = true else showAddSubjectDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_content")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
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
                    text = { Text("Branches (${branches.size})") },
                    icon = { Icon(imageVector = Icons.Default.Domain, contentDescription = "Branches") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Subjects (${allSubjects.size})") },
                    icon = { Icon(imageVector = Icons.Default.MenuBook, contentDescription = "Subjects") }
                )
            }

            if (selectedTab == 0) {
                // BRANCHES LIST
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(branches, key = { it.id }) { branch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = branch.code,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = branch.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    if (branch.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = branch.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row {
                                    IconButton(onClick = { branchToEdit = branch }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit branch",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(onClick = { branchToDelete = branch }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete branch",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // SUBJECTS LIST WITH BRANCH / SEMESTER FILTER
                Column(modifier = Modifier.fillMaxSize()) {
                    // Filter bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Branch Selector
                        var branchDropdownExpanded by remember { mutableStateOf(false) }
                        val currentFilterBranch = branches.firstOrNull { it.id == filterBranchId }

                        ExposedDropdownMenuBox(
                            expanded = branchDropdownExpanded,
                            onExpandedChange = { branchDropdownExpanded = !branchDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = currentFilterBranch?.code ?: "Select",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchDropdownExpanded) },
                                label = { Text("Branch") },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = branchDropdownExpanded,
                                onDismissRequest = { branchDropdownExpanded = false }
                            ) {
                                branches.forEach { b ->
                                    DropdownMenuItem(
                                        text = { Text("${b.code} - ${b.name}") },
                                        onClick = {
                                            filterBranchId = b.id
                                            branchDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Semester Selector
                        var semDropdownExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = semDropdownExpanded,
                            onExpandedChange = { semDropdownExpanded = !semDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = "Sem $filterSemesterNum",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = semDropdownExpanded) },
                                label = { Text("Semester") },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = semDropdownExpanded,
                                onDismissRequest = { semDropdownExpanded = false }
                            ) {
                                (1..6).forEach { sem ->
                                    DropdownMenuItem(
                                        text = { Text("Semester $sem") },
                                        onClick = {
                                            filterSemesterNum = sem
                                            semDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    val targetSemId = "${filterBranchId}_sem_$filterSemesterNum"
                    val filteredSubjects = allSubjects.filter {
                        it.branchId == filterBranchId && it.semesterId == targetSemId
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (filteredSubjects.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No subjects found for this semester. Click '+' to add one.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(filteredSubjects, key = { it.id }) { subject ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = subject.code,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = subject.name,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            if (subject.description.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = subject.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Row {
                                            IconButton(onClick = { subjectToEdit = subject }) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit subject",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            IconButton(onClick = { subjectToDelete = subject }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete subject",
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
        }
    }

    // --- ADD BRANCH DIALOG ---
    if (showAddBranchDialog) {
        var branchName by remember { mutableStateOf("") }
        var branchCode by remember { mutableStateOf("") }
        var branchDesc by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddBranchDialog = false },
            title = { Text("Add New Diploma Branch") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = branchCode,
                        onValueChange = { branchCode = it },
                        label = { Text("Branch Code (e.g. CSE, ME, IT)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = branchName,
                        onValueChange = { branchName = it },
                        label = { Text("Branch Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = branchDesc,
                        onValueChange = { branchDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMsg != null) {
                        Text(text = errorMsg ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (branchCode.isBlank() || branchName.isBlank()) {
                            errorMsg = "Code and Name cannot be empty"
                        } else {
                            viewModel.addBranch(branchName.trim(), branchCode.trim(), "computer", branchDesc.trim())
                            showAddBranchDialog = false
                        }
                    }
                ) {
                    Text("Add Branch")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddBranchDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- EDIT BRANCH DIALOG ---
    if (branchToEdit != null) {
        val branch = branchToEdit!!
        var editName by remember { mutableStateOf(branch.name) }
        var editCode by remember { mutableStateOf(branch.code) }
        var editDesc by remember { mutableStateOf(branch.description) }

        AlertDialog(
            onDismissRequest = { branchToEdit = null },
            title = { Text("Edit Branch ${branch.code}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editCode,
                        onValueChange = { editCode = it },
                        label = { Text("Branch Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Branch Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateBranch(branch.copy(name = editName.trim(), code = editCode.trim(), description = editDesc.trim()))
                        branchToEdit = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { branchToEdit = null }) { Text("Cancel") }
            }
        )
    }

    // --- DELETE BRANCH DIALOG ---
    if (branchToDelete != null) {
        AlertDialog(
            onDismissRequest = { branchToDelete = null },
            title = { Text("Delete Branch ${branchToDelete?.code}?") },
            text = { Text("Deleting this branch will remove all its semesters, subjects, and associated notes.") },
            confirmButton = {
                Button(
                    onClick = {
                        branchToDelete?.let { viewModel.deleteBranch(it.id) }
                        branchToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { branchToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // --- ADD SUBJECT DIALOG ---
    if (showAddSubjectDialog) {
        var subName by remember { mutableStateOf("") }
        var subCode by remember { mutableStateOf("") }
        var subDesc by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        val targetSemId = "${filterBranchId}_sem_$filterSemesterNum"

        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Add Subject (Sem $filterSemesterNum)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = subCode,
                        onValueChange = { subCode = it },
                        label = { Text("Subject Code (e.g. CS-304)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subName,
                        onValueChange = { subName = it },
                        label = { Text("Subject Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subDesc,
                        onValueChange = { subDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (errorMsg != null) {
                        Text(text = errorMsg ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (subCode.isBlank() || subName.isBlank()) {
                            errorMsg = "Code and Name cannot be blank"
                        } else {
                            viewModel.addSubject(
                                branchId = filterBranchId,
                                semesterId = targetSemId,
                                name = subName.trim(),
                                code = subCode.trim(),
                                description = subDesc.trim()
                            )
                            showAddSubjectDialog = false
                        }
                    }
                ) {
                    Text("Add Subject")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddSubjectDialog = false }) { Text("Cancel") }
            }
        )
    }

    // --- EDIT SUBJECT DIALOG ---
    if (subjectToEdit != null) {
        val subject = subjectToEdit!!
        var editSubName by remember { mutableStateOf(subject.name) }
        var editSubCode by remember { mutableStateOf(subject.code) }
        var editSubDesc by remember { mutableStateOf(subject.description) }

        AlertDialog(
            onDismissRequest = { subjectToEdit = null },
            title = { Text("Edit Subject ${subject.code}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editSubCode,
                        onValueChange = { editSubCode = it },
                        label = { Text("Subject Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editSubName,
                        onValueChange = { editSubName = it },
                        label = { Text("Subject Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editSubDesc,
                        onValueChange = { editSubDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSubject(subject.copy(name = editSubName.trim(), code = editSubCode.trim(), description = editSubDesc.trim()))
                        subjectToEdit = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { subjectToEdit = null }) { Text("Cancel") }
            }
        )
    }

    // --- DELETE SUBJECT DIALOG ---
    if (subjectToDelete != null) {
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Delete Subject ${subjectToDelete?.code}?") },
            text = { Text("Deleting this subject will also delete all notes under it.") },
            confirmButton = {
                Button(
                    onClick = {
                        subjectToDelete?.let { viewModel.deleteSubject(it.id) }
                        subjectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { subjectToDelete = null }) { Text("Cancel") }
            }
        )
    }
}
