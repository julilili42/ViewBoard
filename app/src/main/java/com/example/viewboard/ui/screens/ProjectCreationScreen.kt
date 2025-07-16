package com.example.viewboard.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.FirebaseProvider
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.viewboard.ui.navigation.ChipInputField

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProjectCreationScreen(
    navController: NavController,
    onCreate: () -> Unit = {}
) {
    val uiColor = uiColor()
    val scroll = rememberScrollState()

    // Eingabe-States
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    // Datum-Range-States
    var startDate by remember { mutableStateOf("") }
    var endDate   by remember { mutableStateOf("") }

    // Teilnehmer-Chips
    var assignments by remember { mutableStateOf(listOf<String>()) }
    var newParticipant by remember { mutableStateOf("") }

    // Meilensteine
    var totalMilestones by remember { mutableStateOf(0) }

    // Validierungs-States
    val datePattern = Regex("\\d{4}-\\d{2}-\\d{2}")
    val isNameValid by derivedStateOf { name.isNotBlank() }
    val isDateRangeValid by derivedStateOf {
        datePattern.matches(startDate) &&
                datePattern.matches(endDate) &&
                runCatching {
                    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    fmt.parse(startDate)!!.time <= fmt.parse(endDate)!!.time
                }.getOrDefault(false)
    }
    val isTotalMilestonesValid by derivedStateOf { totalMilestones > 0 }
    val allValid by derivedStateOf {
        isNameValid && isDateRangeValid && isTotalMilestonesValid
    }

    // DatePicker für Range
    val context = LocalContext.current
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    fun pickDateRange() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y1, m1, d1 ->
                calendar.set(y1, m1, d1)
                startDate = formatter.format(calendar.time)
                // nun Enddatum
                DatePickerDialog(
                    context,
                    { _, y2, m2, d2 ->
                        calendar.set(y2, m2, d2)
                        endDate = formatter.format(calendar.time)
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
            TopAppBar(
                title = { Text("New Project", color = uiColor) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = uiColor)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                isError = !isNameValid && name.isNotBlank(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            if (!isNameValid && name.isNotBlank()) {
                Text("Name is not allowed to be empty", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // Beschreibung
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp),
                maxLines = Int.MAX_VALUE,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(Modifier.height(16.dp))

            // Zeitraum
            OutlinedTextField(
                value = if (startDate.isNotBlank() && endDate.isNotBlank())
                    "$startDate – $endDate" else "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Time period") },
                isError = !isDateRangeValid && (startDate.isNotBlank() || endDate.isNotBlank()),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.calender_svgrepo_com),
                        contentDescription = "Pick time period",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { pickDateRange() },
                        tint = uiColor
                    )
                }
            )
            if (!isDateRangeValid && (startDate.isNotBlank() || endDate.isNotBlank())) {
                Text("Invalid time period", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))

            // Teilnehmer
            ChipInputField(
                entries = assignments,
                newEntry = newParticipant,
                inhaltText = "Add team member..-",
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
            Spacer(Modifier.height(24.dp))

            // Meilensteine
            Text("Milestone", style = MaterialTheme.typography.labelLarge, color = uiColor)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                IconButton(
                    onClick = { if (totalMilestones > 0) totalMilestones-- },
                    modifier = Modifier
                        .size(40.dp)
                        .background(uiColor.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.minus_svgrepo_com),
                        contentDescription = "Less",
                        tint = uiColor
                    )
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = totalMilestones.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = uiColor
                )
                Spacer(Modifier.width(16.dp))
                IconButton(
                    onClick = { totalMilestones++ },
                    modifier = Modifier
                        .size(40.dp)
                        .background(uiColor.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.plus_large_svgrepo_com),
                        contentDescription = "More",
                        tint = uiColor
                    )
                }
            }
            if (!isTotalMilestonesValid) {
                Text("At least one milestone necessary", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(32.dp))

            // Create-Button mit FirebaseAPI-Aufruf
            Button(
                onClick = {
                    // 1) Monate extrahieren
                    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val cal1 = Calendar.getInstance().apply { time = fmt.parse(startDate)!! }
                    val cal2 = Calendar.getInstance().apply { time = fmt.parse(endDate)!! }
                    val startMonth = cal1.get(Calendar.MONTH) + 1
                    val endMonth   = cal2.get(Calendar.MONTH) + 1

                    // 2) Projekt-Objekt bauen
                    val p = ProjectLayout(
                        name = name,
                        desc = desc,
                        creator ="",
                        phase = "",
                        startMonth = startMonth,
                        endMonth = endMonth,
                        totalMilestones = totalMilestones,
                        completedMilestones = 0f,
                        issues = arrayListOf(),
                        labels = arrayListOf(),
                        views = arrayListOf(),
                        users = ArrayList(assignments),
                    )

                    val user = FirebaseProvider.auth.currentUser
                    if (user != null) {
                        FirebaseAPI.addProject(p)
                        navController.popBackStack()
                        onCreate()
                    }
                },
                enabled = allValid,
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