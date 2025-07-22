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
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.ChipInputField
import com.example.viewboard.ui.theme.uiColor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueEditScreen(
    navController: NavController,
    projectId: String,
    issue: IssueLayout,
    onUpdated: () -> Unit = {}
) {
    val uiColor = uiColor()
    val context = LocalContext.current
    val scroll = rememberScrollState()

    // Initial states
    var title by remember { mutableStateOf(issue.title) }
    var desc by remember { mutableStateOf(issue.desc) }
    var assignments by remember { mutableStateOf(issue.assignments.toList()) }



    val calendar = remember {
        Calendar.getInstance().apply {
            val instant = Instant.parse(issue.deadlineTS)
            time = Date.from(instant)
        }
    }
    val dateFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    var dateText by remember { mutableStateOf(dateFmt.format(calendar.time)) }
    var timeText by remember { mutableStateOf(timeFmt.format(calendar.time)) }
    var newDeadlineTS by remember { mutableStateOf(issue.deadlineTS) }
    val scope = rememberCoroutineScope()

    fun updateDeadline() {
        val d = dateFmt.parse(dateText) ?: return
        val t = timeFmt.parse(timeText) ?: return
        calendar.time = d
        val tmp = Calendar.getInstance().apply { time = t }
        calendar.set(Calendar.HOUR_OF_DAY, tmp.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, tmp.get(Calendar.MINUTE))
        val ts = Timestamp().apply { import(calendar.toInstant()) }
        newDeadlineTS = ts.export()
    }

    fun pickDate() {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                calendar.set(y, m, d)
                dateText = dateFmt.format(calendar.time)
                updateDeadline()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun pickTime() {
        TimePickerDialog(
            context,
            { _, h, min ->
                calendar.set(Calendar.HOUR_OF_DAY, h)
                calendar.set(Calendar.MINUTE, min)
                timeText = timeFmt.format(calendar.time)
                updateDeadline()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Issue", color = uiColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = uiColor)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Spacer(Modifier.height(12.dp))
            ChipInputField(
                entries = assignments,
                newEntry = "",
                inhaltText = "Add Assignee…",
                onNewEntryChange = {},
                onEntryConfirmed = { /* implement */ },
                onEntryRemove = { /* implement */ },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date") },
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.clickable { pickDate() }
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = timeText,
                    onValueChange = {
                        timeText = it
                        updateDeadline()
                    },
                    label = { Text("Time") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = { updateDeadline() }
                    ),
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, null,
                            Modifier.clickable { pickTime() })
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    updateDeadline()
                    val updatedIssue = issue.copy(
                        title = title.capitalizeWords(),
                        desc = desc,
                        assignments = ArrayList(assignments),
                        labels = ArrayList(),
                        deadlineTS = newDeadlineTS
                    )
                    FirebaseAPI.updIssue(
                        updatedIssue,
                        onSuccess = { id ->
                            onUpdated()
                            navController.popBackStack()
                        },
                        onFailure = { id -> }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save Changes")
            }
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Delete Issue",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        scope.launch {
                            FirebaseAPI.rmIssue(
                                projID = projectId,
                                id = issue.id,
                                onSuccess = { _ -> navController.popBackStack() },
                                onFailure = { _ -> /* Fehlerbehandlung */ }
                            )
                        }
                    },
                color = MaterialTheme.colorScheme.error
            )

        }
    }
}

