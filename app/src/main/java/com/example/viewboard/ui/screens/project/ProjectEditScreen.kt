package com.example.viewboard.ui.screens.project

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.backend.auth.impl.AuthAPI.getListOfAllUsers
import com.example.viewboard.backend.auth.impl.FirebaseProvider.auth
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.UserLayout
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.utils.ChipInputField
import com.example.viewboard.ui.theme.uiColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditScreen(
    navController: NavController,
    projectId: String,
    project: ProjectLayout,
    onUpdated: () -> Unit = {}
) {
    val uiColor = uiColor()
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUserId = auth.currentUser?.uid
    val isCreator = currentUserId == project.creator
    var users by remember { mutableStateOf<List<UserLayout>>(emptyList()) }
    LaunchedEffect(users) {
        try {
            val result: Result<List<UserLayout>> = getListOfAllUsers()
            users = result.getOrNull() ?: emptyList()
        } catch (e: Exception) {
        }
    }
    var name by remember { mutableStateOf(project.name) }
    var desc by remember { mutableStateOf("") } // TODO remove
    var startDate by remember { mutableStateOf(project.startTS) }
    var endDate by remember { mutableStateOf(project.deadlineTS) }

    val names = project.users.toList().mapNotNull { email ->
        users.find { it.id .equals(email, ignoreCase = true) }?.email
    }
    var assignments by remember(names) {
        mutableStateOf(names)
    }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun pickDateRange() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y1, m1, d1 ->
                calendar.set(y1, m1, d1)
                startDate = dateFormat.format(calendar.time)
                DatePickerDialog(
                    context,
                    { _, y2, m2, d2 ->
                        calendar.set(y2, m2, d2)
                        endDate = dateFormat.format(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit project", color = uiColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = uiColor)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isCreator
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isCreator
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = if (startDate.isNotBlank() && endDate.isNotBlank())
                    "$startDate – $endDate" else "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Timeframe") },
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Choose Date",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(enabled = isCreator) { pickDateRange() },
                        tint = uiColor
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isCreator
            )
            Spacer(Modifier.height(12.dp))


            var newParticipant by remember { mutableStateOf("") }

            val filteredUsers = remember(newParticipant, users) {
                if (newParticipant.isBlank()) emptyList()
                else users.filter { user ->
                    user.name.contains(newParticipant, ignoreCase = true) ||
                            user.email.contains(newParticipant, ignoreCase = true)
                }
            }
            val suggestionEmails = filteredUsers.map { it.email }
            val suggestionList = remember(newParticipant, assignments, suggestionEmails) {
                if (newParticipant.isBlank()) {
                    emptyList()
                } else {
                    suggestionEmails.filter { email ->
                        // includes entered letters
                        email.contains(newParticipant, ignoreCase = true)
                                // and not in assignments
                                && assignments.none { it.equals(email, ignoreCase = true) }
                    }
                }
            }
            ChipInputField(
                entries = assignments,
                newEntry = newParticipant,
                contentText = "Add team member…",
                suggestions = suggestionList,
                onSuggestionClick = { name ->
                    // if name is selected add as chip
                    if (name !in assignments) {
                        assignments = assignments + name
                    }
                    newParticipant = ""
                },
                onNewEntryChange = { newParticipant = it },
                onEntryConfirmed = {
                },
                onEntryRemove = { removed ->
                    assignments = assignments - removed
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            val assignmentIds: ArrayList<String> = remember(assignments, users) {
                val ids = assignments.mapNotNull { email ->
                    users.find { it.email.equals(email, ignoreCase = true) }?.id
                }
                ArrayList(ids)
            }
            if (isCreator) {
                Button(
                    onClick = {
                        val updated = project.copy(
                            name = name,
                            users = ArrayList(assignmentIds),
                            startTS = startDate,
                            deadlineTS = endDate
                        )
                        FirebaseAPI.updProject(
                            updated,
                            onSuccess = {
                                onUpdated()
                                navController.popBackStack()
                            },
                            onFailure = { /* TODO Error handling */ }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Safe")
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    "Delete Project",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                FirebaseAPI.rmProject(
                                    id = project.id,
                                    onSuccess = { navController.popBackStack() },
                                    onFailure = { /* TODO Error handling */ }
                                )
                            }
                        }
                        .padding(8.dp)
                )
            } else {
                Text("Only the creator can edit or delete this project.\n",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}
