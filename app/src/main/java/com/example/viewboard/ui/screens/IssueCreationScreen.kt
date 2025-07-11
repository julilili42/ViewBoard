package com.example.viewboard.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.ui.theme.uiColor
import com.example.viewboard.backend.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.rememberCoroutineScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import kotlinx.coroutines.launch
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.LabelLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.ChipInputField


// ---------------------------------------------------
// Dein CreateIssueScreen
// ---------------------------------------------------
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueCreationScreen(
    navController: NavController,
    projectId: String = "ysZaMVY24jnSyLuE5FKJ",
    currentUserId: String = AuthAPI.getUid() ?: "",

    onCreate: () -> Unit = {}
) {

    val uiColor = uiColor()
    val context = LocalContext.current
    val scroll = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    var assignments by remember { mutableStateOf(listOf<String>()) }
    var newParticipant by remember { mutableStateOf("") }

    var labels by remember { mutableStateOf(listOf<String>()) }
    var newLabelName by remember { mutableStateOf("") }

    val calendar = remember { Calendar.getInstance() }
    var deadline by remember {
        mutableStateOf(Timestamp().apply { import(calendar.toInstant()) })
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    var dateText by remember { mutableStateOf(dateFormatter.format(calendar.time)) }
    var timeText by remember { mutableStateOf(timeFormatter.format(calendar.time)) }

    // Validität prüfen
    val isDateValid by derivedStateOf {
        try { dateFormatter.parse(dateText); true } catch (_: Exception) { false }
    }
    val isTimeValid by derivedStateOf {
        try { timeFormatter.parse(timeText); true } catch (_: Exception) { false }
    }
    val coroutineScope = rememberCoroutineScope()

    fun updateDeadlineFromDateText() {
        val d = dateFormatter.parse(dateText) ?: throw IllegalArgumentException("Ungültiges Datum")
        calendar.time = d
        val ts = Timestamp()
        ts.import(d.toInstant())
        deadline = ts
    }


    fun updateDeadlineFromTimeText() {
        val t = timeFormatter.parse(timeText) ?: throw IllegalArgumentException("Ungültige Uhrzeit")
        val cal2 = Calendar.getInstance().apply { time = t }

        calendar.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE))

        val ts = Timestamp()
        ts.import(calendar.toInstant())
        deadline = ts
    }


    fun pickDate() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val ts = Timestamp()
                ts.import(calendar.toInstant())
                deadline = ts
                dateText = dateFormatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun pickTime() {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val ts = Timestamp()
                ts.import(calendar.toInstant())
                deadline = ts
                timeText = timeFormatter.format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("New Issue", color = uiColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = uiColor)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding .calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(bottom =16.dp , top = 0.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp),
                maxLines = Int.MAX_VALUE
            )
            Spacer(Modifier.height(16.dp))

            ChipInputField(
                entries = assignments,
                newEntry = newParticipant,
                inhaltText = "Add Assignee…",
                onNewEntryChange = { newParticipant = it },
                onEntryConfirmed = {
                    if (newParticipant.isNotBlank()) {
                        assignments = assignments + newParticipant.trim()
                        newParticipant = ""
                    }
                },
                onEntryRemove = { removed -> assignments = assignments - removed },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            ChipInputField(
                entries = labels,
                newEntry = newLabelName,
                inhaltText = "Add Label…",
                onNewEntryChange = { newLabelName = it },
                onEntryConfirmed = {
                    if (newLabelName.isNotBlank()) {
                        labels = labels + newLabelName.trim()
                        newLabelName = ""
                    }
                },
                onEntryRemove = { removed -> labels = labels - removed },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Datum (YYYY-MM-DD)") },
                singleLine = true,
                isError = !isDateValid,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { updateDeadlineFromDateText() }),
                trailingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Datum wählen",
                        modifier = Modifier.clickable { pickDate() },
                        tint = uiColor
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = timeText,
                onValueChange = { timeText = it },
                label = { Text("Uhrzeit (HH:mm)") },
                singleLine = true,
                isError = !isTimeValid,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { updateDeadlineFromTimeText() }),
                trailingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Uhrzeit wählen",
                        modifier = Modifier.clickable { pickTime() },
                        tint = uiColor
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            if (!isDateValid) {
                Text(
                    text = "Ungültiges Datum",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!isTimeValid) {
                Text(
                    text = "Ungültige Uhrzeit",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validierung
                    if (isDateValid && isTimeValid) {
                        // Datum/Uhrzeit ins Modell übernehmen
                        updateDeadlineFromDateText()
                        updateDeadlineFromTimeText()
                        val labelObjects = ArrayList(labels.map { labelName ->
                            LabelLayout(
                                name    = labelName,
                                creator = currentUserId
                            )
                        })
                        // Issue anlegen und speichern
                        val newIssue = IssueLayout(
                            title       = title.trim(),
                            desc        = desc.trim(),
                            creator     = currentUserId,
                            assignments = ArrayList(assignments),
//                            labels      = labelObjects,
                            labels      = ArrayList(labels),
                            deadlineTS  = deadline.export()
                        )


                        coroutineScope.launch {
                            try {
                                FirebaseAPI.addIssue(projID = projectId, issueLayout = newIssue)
                                navController.popBackStack()
                            } catch (e: Exception) {
                                // z.B. Fehler anzeigen:
                                errorMessage = "Speichern fehlgeschlagen: ${e.localizedMessage}"
                            }
                        }
                    }
                },
                enabled = isDateValid && isTimeValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                )
            ) {
                Text("Create", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}


