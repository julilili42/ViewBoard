package com.example.viewboard.frontend.screens.issue

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
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
import com.example.viewboard.backend.time.Timestamp
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.EmailWithId
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.frontend.navigation.utils.ChipInputField
import com.example.viewboard.frontend.components.theme.uiColor
import com.example.viewboard.frontend.components.utils.capitalizeWords
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("MutableCollectionMutableState")
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

    var title by remember { mutableStateOf(issue.title) }

    var labels by remember { mutableStateOf(issue.labels) }
    var newLabelName by remember { mutableStateOf("") }
    var project by remember { mutableStateOf<ProjectLayout?>(null) }
    var newParticipant by remember { mutableStateOf("") }

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
    val mailsOfIssueUsers: List<String> = pairedObjects
        .filter { it.userId in issue.users }
        .map { it.mail.toString() }

    var users by remember(mailsOfIssueUsers) { mutableStateOf(mailsOfIssueUsers) }
    val assignmentIds: ArrayList<String> = remember(users, pairedObjects) {
        val ids = users.mapNotNull { email ->
            pairedObjects
                .find { it.mail.equals(email, ignoreCase = true) }
                ?.userId
        }
        ArrayList(ids)
    }

    val calendar = remember {
        Calendar.getInstance().apply {
            val instant = Instant.parse(issue.deadlineTS)
            time = Date.from(instant)
        }
    }

    val emails = emailsState.orEmpty()
        .filterNotNull()
    Log.d("emails", "emails=${emails}")

    val suggestionList = remember(newParticipant, users, emails) {
        if (newParticipant.isBlank()) {
            emptyList()
        } else {
            emails.filter { email ->
                email.contains(newParticipant, ignoreCase = true)
                        && users.none { it.equals(email, ignoreCase = true) }
            }
        }
    }

    val dateFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    var dateText by remember { mutableStateOf(dateFmt.format(calendar.time)) }
    var timeText by remember { mutableStateOf(timeFmt.format(calendar.time)) }
    var newDeadlineTS by remember { mutableStateOf(issue.deadlineTS) }

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
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        updateDeadline()
                        val updatedIssue = issue.copy(
                            title = title.capitalizeWords(),
                            labels = labels,
                            users = ArrayList(assignmentIds),
                            deadlineTS = newDeadlineTS
                        )
                        FirebaseAPI.updIssue(
                            updatedIssue,
                            onSuccess = { _ -> onUpdated() },
                            onFailure = { _ ->
                                Log.e(
                                    "IssueEdit",
                                    "Error while saving ${issue.id}"
                                )
                            }
                        )
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Save Changes")
                }
            }
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
            Spacer(Modifier.height(24.dp))

            ChipInputField(
                entries = users,
                newEntry = newParticipant,
                contentText = "Add team Assignee…",
                suggestions = suggestionList,
                onSuggestionClick = { name ->
                    if (name !in users) {
                        users = (users + name) as ArrayList<String>
                    }
                    newParticipant = ""
                },
                onNewEntryChange = { newParticipant = it },
                onEntryConfirmed = {

                },
                onEntryRemove = { removed ->
                    users = (users - removed) as ArrayList<String>
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
                        labels = (labels + newLabelName.trim()) as ArrayList<String>
                        newLabelName = ""
                    }
                },
                onEntryRemove = { removed -> labels = (labels - removed) as ArrayList<String> },
                modifier = Modifier.fillMaxWidth()
            )

            if (newLabelName.isNotBlank()) {
                Text(
                    text = "Press enter to add label",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.Start)
                )
            }


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
                            Modifier.clickable { pickDate() }
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
                        Icon(
                            Icons.Default.DateRange, null,
                            Modifier.clickable { pickTime() })
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
