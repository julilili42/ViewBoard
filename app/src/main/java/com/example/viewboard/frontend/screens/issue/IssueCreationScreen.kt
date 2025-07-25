package com.example.viewboard.frontend.screens.issue

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.viewboard.frontend.components.theme.uiColor
import com.example.viewboard.backend.util.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.rememberCoroutineScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.EmailWithId
import kotlinx.coroutines.launch
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.frontend.navigation.utils.ChipInputField
import com.example.viewboard.frontend.components.utils.capitalizeWords

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueCreationScreen(
    navController: NavController,
    projectId: String,
    currentUserId: String = AuthAPI.getUid() ?: "",
) {
    val uiColor = uiColor()
    val context = LocalContext.current
    val scroll = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    var users by remember { mutableStateOf(listOf<String>()) }
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

    val isDateValid by derivedStateOf {
        try { dateFormatter.parse(dateText); true } catch (_: Exception) { false }
    }

    val isTimeValid by derivedStateOf {
        try { timeFormatter.parse(timeText); true } catch (_: Exception) { false }
    }

    val coroutineScope = rememberCoroutineScope()

    fun updateDeadlineFromDateText() {
        val d = dateFormatter.parse(dateText) ?: throw IllegalArgumentException("Invalid date")
        calendar.time = d
        val ts = Timestamp()
        ts.import(d.toInstant())
        deadline = ts
    }

    var project by remember { mutableStateOf<ProjectLayout?>(null) }

    fun updateDeadlineFromTimeText() {
        val t = timeFormatter.parse(timeText) ?: throw IllegalArgumentException("Invalid date")
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

    LaunchedEffect(projectId) {
        val cleanId = projectId.trim('{', '}')
        try {
            project = FirebaseAPI
                .getProject(cleanId)

        } catch (e: Exception) {
            Log.e("IssueCreationScreen", "Error fetching project: ${e.message}")
        }
    }

    val emailsState by produceState<List<String?>>(
        initialValue = emptyList(),
        key1 = project?.users
    ) {
        val result = runCatching { project?.let { AuthAPI.getEmailsByIds(it.users) } }
            .getOrNull()
            ?.getOrNull()
        value = result ?: emptyList()
    }

    val pairedObjects: List<EmailWithId> =
        (project?.users ?: emptyList()).zip(emailsState) { id, mail ->
            EmailWithId(userId = id, mail = mail)
        }

    val emails = emailsState.orEmpty()
        .filterNotNull()
    Log.d("emails", "emails=${emails}")
    val suggestionList = remember(newParticipant, users, emails ) {
        if (newParticipant.isBlank()) {
            emptyList()
        } else {
            emails.filter { email ->
                email.contains(newParticipant, ignoreCase = true)
                        && users.none { it.equals(email, ignoreCase = true) }
            }
        }
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
                .padding(top = innerPadding.calculateTopPadding())
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
            Spacer(Modifier.height(24.dp))


                ChipInputField(
                    entries = users,
                    newEntry = newParticipant,
                    contentText = "Add Assignee…",
                    suggestions = suggestionList,
                    onSuggestionClick = { name ->
                        if (name !in users) {
                            users = users + name
                        }
                        newParticipant = ""
                    },
                    onNewEntryChange = { newParticipant = it },
                    onEntryConfirmed = {

                    },
                    onEntryRemove = { removed ->
                        users = users - removed
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            Spacer(Modifier.height(12.dp))
            ChipInputField(
                entries = labels,
                newEntry = newLabelName,
                contentText = "Add Label…",
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
            Spacer(Modifier.height(24.dp))

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
            val assignmentIds: ArrayList<String> = remember(users, pairedObjects) {
                val ids = users.mapNotNull { email ->
                    pairedObjects
                        .find { it.mail.equals(email, ignoreCase = true) }
                        ?.userId
                }
                ArrayList(ids)
            }
            Button(
                enabled = isDateValid && isTimeValid&& isFormValid,
                onClick = {
                    if (isDateValid && isTimeValid) {
                        updateDeadlineFromDateText()
                        updateDeadlineFromTimeText()
                        val newIssue = IssueLayout(
                            title       = title.capitalizeWords(),
                            desc        = desc,
                            creator     = currentUserId,
                            users = assignmentIds,
                            projID = projectId,
                            deadlineTS  = deadline.export()
                        )
                        coroutineScope.launch {
                            try {
                                val cleanId = projectId.trim('{', '}')
                                FirebaseAPI.addIssue(projID = cleanId , issueLayout = newIssue)
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


