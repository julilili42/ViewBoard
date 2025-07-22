package com.example.viewboard.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
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
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.ChipInputField
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// ---------------------------------------------------
// Dein CreateIssueScreen
// ---------------------------------------------------
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueCreationScreen(
    navController: NavController,
    projectId: String,
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

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    var dateText by remember { mutableStateOf(dateFormatter.format(calendar.time)) }
    var timeText by remember { mutableStateOf(timeFormatter.format(calendar.time)) }
    val isTitleValid by derivedStateOf { title.trim().isNotEmpty() }
    val isDescValid  by derivedStateOf { desc.trim().isNotEmpty() }

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
    val isoFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")
            .withZone(ZoneOffset.UTC)
    fun updateDeadlineFromDateTimeText(dateText: String, timeText: String) {
        // 1) Datum parsen
        val d = dateFormatter.parse(dateText)
            ?: throw IllegalArgumentException("Ungültiges Datum")
        // 2) Zeit parsen
        val t = timeFormatter.parse(timeText)
            ?: throw IllegalArgumentException("Ungültige Uhrzeit")

        // 3) Kalender initialisieren mit Datum
        val cal = Calendar.getInstance().apply { time = d }
        // 4) Stunden und Minuten setzen
        val calTime = Calendar.getInstance().apply { time = t }
        cal.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE,    calTime.get(Calendar.MINUTE))
        cal.set(Calendar.SECOND,    calTime.get(Calendar.SECOND))
        cal.set(Calendar.MILLISECOND, calTime.get(Calendar.MILLISECOND))

        // 5) Instant erzeugen und ins Timestamp‑Objekt schieben
        val instant = cal.toInstant()
        val ts = Timestamp().apply { import(instant) }
        deadline = ts

        // 6) Optional: ISO‑String
        val isoString = isoFormatter.format(instant)
        Log.d("Deadline", "Neue Deadline: $isoString")
        // isoString enthält z.B. "2025-07-21T18:11:34.902588Z"
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

            var project by remember { mutableStateOf<ProjectLayout?>(null) }

            // 2) Zugehöriges Projekt nur einmal beim ersten Compose laden
            LaunchedEffect(projectId) {
                val cleanId = projectId.trim('{', '}')
                try {
                    project = FirebaseAPI
                        .getProject(cleanId)   // Flow<ProjectLayout>

                } catch (e: Exception) {
                    Log.e("ProjectDetailInline", "Fehler beim Laden von $cleanId", e)
                }
            }

            val emailsState by produceState<List<String?>>(
                initialValue = emptyList(),
                key1 = project?.users
            ) {
                // Lade die E‑Mails; bei Fehler oder leerem Ergebnis bleibt es bei emptyList
                val result = runCatching { project?.let { AuthAPI.getEmailsByIds(it.users) } }
                    .getOrNull()
                    ?.getOrNull()
                value = result ?: emptyList()
            }
            data class EmailWithId(val userId: String, val mail: String?)
            val pairedObjects: List<EmailWithId> =
                (project?.users ?: emptyList()).zip(emailsState) { id, mail ->
                    EmailWithId(userId = id, mail = mail)
                }

            emailsState
            val suggestionEmails = project?.users.orEmpty()
            val suggestionTags = project?.labels
            val emails = emailsState.orEmpty()
                .filterNotNull()
            Log.d("emails", "emails=${emails}")
            val suggestionList = remember(newParticipant, assignments, emails ) {
                if (newParticipant.isBlank()) {
                    emptyList()
                } else {
                    emails.filter { email ->
                        // enthält eingegebene Zeichen
                        email.contains(newParticipant, ignoreCase = true)
                                // und ist noch nicht in assignments
                                && assignments.none { it.equals(email, ignoreCase = true) }
                    }
                }
            }

            val allNames = listOf("Alice", "Bob", "Charlie", "David")

            if (suggestionList != null) {
                ChipInputField(
                    entries = assignments,
                    newEntry = newParticipant,
                    inhaltText = "Add team member…",
                    suggestions = suggestionList,
                    onSuggestionClick = { name ->
                        // wenn Name ausgewählt wird, als Chip hinzufügen
                        if (name !in assignments) {
                            assignments = assignments + name
                        }
                        newParticipant = ""
                    },
                    onNewEntryChange = { newParticipant = it },
                    onEntryConfirmed = {
                        // Option: Akzeptiere nur exakte Übereinstimmung
                        val match = allNames.find { it.equals(newParticipant.trim(), ignoreCase = true) }
                        if (match != null && match !in assignments) {
                            assignments = assignments + match
                        }
                        newParticipant = ""
                    },
                    onEntryRemove = { removed ->
                        assignments = assignments - removed
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }


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
                    onValueChange = { timeText = it },
                    label = { Text("Time") },
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.clickable { pickTime() }
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))

            if (!isDateValid) {
                Text(
                    text = "Invalid date",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!isTimeValid) {
                Text(
                    text = "Invalid time",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(16.dp))
            val isFormValid by derivedStateOf {
                isTitleValid
            }
            val assignmentIds: ArrayList<String> = remember(assignments, pairedObjects) {
                val ids = assignments.mapNotNull { email ->
                    pairedObjects
                        .find { it.mail.equals(email, ignoreCase = true) }
                        ?.userId
                }
                ArrayList(ids)
            }
            Button(

                enabled = isDateValid && isTimeValid&& isFormValid,
                onClick = {
                    // Validierung

                    if (isDateValid && isTimeValid) {
                        // Datum/Uhrzeit ins Modell übernehmen
                        updateDeadlineFromDateText()
                        updateDeadlineFromTimeText()
                        // Issue anlegen und speichern
                        val newIssue = IssueLayout(
                            title       = title.capitalizeWords(),
                            desc        = desc,
                            creator     = currentUserId,
                            assignments = assignmentIds,
                            projectid = projectId,
//                            labels      = labelObjects,
                            labels      = ArrayList(labels),
                            deadlineTS  = deadline.export()
                        )
                        coroutineScope.launch {
                            try {
                                val cleanId = projectId.trim('{', '}')
                                FirebaseAPI.addIssue(projID = cleanId , issueLayout = newIssue)
                                // addIssue(projID = projectId, issueLayout = newIssue)
                                navController.popBackStack()
                            } catch (e: Exception) {

                            }
                        }
                    }
                },
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


fun String.capitalizeWords(): String =
    this
        .split(Regex("\\s+"))                        // nach Leerraum trennen
        .joinToString(" ") { word ->
            word
                .lowercase()                         // zuerst alles klein
                .replaceFirstChar {                   // dann ersten Buchstaben groß
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
        }