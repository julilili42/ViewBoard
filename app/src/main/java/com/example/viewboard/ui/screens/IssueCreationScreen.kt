package com.example.viewboard.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.ui.theme.uiColor
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import java.text.ParseException


// ---------------------------------------------------
// Dein CreateIssueScreen
// ---------------------------------------------------
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueCreationScreen(
    navController: NavController,
    currentUserId: String = "1",
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
    var deadline by remember { mutableStateOf(Timestamp(calendar.time)) }
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

    fun updateDeadlineFromDateText() {
        val d = dateFormatter.parse(dateText) ?: throw IllegalArgumentException("Ungültiges Datum")
        calendar.time = d
        deadline = Timestamp(calendar.time)
    }

    fun updateDeadlineFromTimeText() {
        val t = timeFormatter.parse(timeText) ?: throw IllegalArgumentException("Ungültige Uhrzeit")
        val cal2 = Calendar.getInstance().apply { time = t }
        calendar.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE))
        deadline = Timestamp(calendar.time)
    }

    fun pickDate() {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                deadline = Timestamp(calendar.time)
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
                deadline = Timestamp(calendar.time)
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
                    updateDeadlineFromDateText()
                    updateDeadlineFromTimeText()
                    onCreate()
                    navController.popBackStack()
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



@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChipInputField(
    entries: List<String>,
    newEntry: String,
    inhaltText: String,
    onNewEntryChange: (String) -> Unit,
    onEntryConfirmed: () -> Unit,
    onEntryRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = newEntry,
        onValueChange = onNewEntryChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onEntryConfirmed() }),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
            .padding(4.dp),
        decorationBox = { innerTextField ->
            Column(modifier = Modifier.fillMaxWidth()) {
                // Zeile 1: Chips
                FlowRow(modifier = Modifier.fillMaxWidth()) {
                    entries.forEach { entry ->
                        AssistChip(
                            onClick = { onEntryRemove(entry) },
                            label = { Text(entry) },
                            modifier = Modifier
                                .height(32.dp)
                                .padding(start = 4.dp, bottom = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Zeile 2: Eingabefeld mit Padding
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newEntry.isEmpty()) {
                        Text(
                            text = inhaltText,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
                        innerTextField()
                    }
                }
            }
        }
    )
}






