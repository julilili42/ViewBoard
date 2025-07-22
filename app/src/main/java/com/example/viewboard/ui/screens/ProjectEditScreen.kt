package com.example.viewboard.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
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
import com.example.viewboard.backend.Timestamp
import com.example.viewboard.backend.auth.impl.FirebaseProvider.auth
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.ChipInputField
import com.example.viewboard.ui.theme.uiColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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

    var name by remember { mutableStateOf(project.name) }
    var desc by remember { mutableStateOf(project.desc) }
    var startDate by remember { mutableStateOf(project.startTS) }
    var endDate by remember { mutableStateOf(project.deadlineTS) }
    var users by remember { mutableStateOf(project.users.toList()) }
    val datePattern = Regex("\\d{4}-\\d{2}-\\d{2}")

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
                title = { Text("Projekt bearbeiten", color = uiColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück", tint = uiColor)
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
                label = { Text("Beschreibung") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isCreator
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = if (startDate.isNotBlank() && endDate.isNotBlank())
                    "$startDate – $endDate" else "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Zeitraum") },
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Datum wählen",
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

            var newUser by remember { mutableStateOf("") }
            ChipInputField(
                entries = users,
                newEntry = newUser,
                inhaltText = "Teilnehmer hinzufügen…",
                onNewEntryChange = { newUser = it },
                onEntryConfirmed = {
                    if (newUser.isNotBlank()) {
                        users = users + newUser
                        newUser = ""
                    }
                },
                onEntryRemove = { entry -> users = users - entry },
                modifier = Modifier.fillMaxWidth(),
                enabled = isCreator
            )

            Spacer(Modifier.height(24.dp))

            if (isCreator) {
                Button(
                    onClick = {
                        val updated = project.copy(
                            name = name,
                            desc = desc,
                            users = ArrayList(users),
                            startTS = startDate,
                            deadlineTS = endDate
                        )
                        FirebaseAPI.updProject(
                            updated,
                            onSuccess = {
                                onUpdated()
                                navController.popBackStack()
                            },
                            onFailure = { /* Fehlerbehandlung */ }
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
                                    onFailure = { /* Fehlerbehandlung */ }
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
