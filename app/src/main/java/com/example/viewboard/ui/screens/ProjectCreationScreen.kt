package com.example.viewboard.ui.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.viewboard.backend.auth.impl.AuthAPI.getListOfAllUsers
import com.example.viewboard.backend.auth.impl.FirebaseProvider
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.UserLayout
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.viewboard.ui.navigation.ChipInputField
import com.example.viewboard.ui.utils.capitalizeWords

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProjectCreationScreen(
    navController: NavController,
    onCreate: () -> Unit = {}
) {
    val uiColor = uiColor()
    val scroll = rememberScrollState()
    var name by remember { mutableStateOf("") }
    val desc by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate   by remember { mutableStateOf("") }
    var assignments by remember { mutableStateOf(listOf<String>()) }
    var users by remember { mutableStateOf<List<UserLayout>>(emptyList()) }
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
    val allValid by derivedStateOf {
        isNameValid && isDateRangeValid
    }

    val context = LocalContext.current
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    fun pickDateRange() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, y1, m1, d1 ->
                calendar.set(y1, m1, d1)
                startDate = formatter.format(calendar.time)
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
            LaunchedEffect(users) {
                try {
                    val result: Result<List<UserLayout>> = getListOfAllUsers()
                    users = result.getOrNull() ?: emptyList()
                } catch (e: Exception) {
                    users = emptyList()
                }
            }
            // Timeframe
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
                        email.contains(newParticipant, ignoreCase = true)
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



            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {

            }
            Spacer(Modifier.height(32.dp))
            val assignmentIds: ArrayList<String> = remember(assignments, users) {
                val ids = assignments.mapNotNull { email ->
                    users.find { it.email.equals(email, ignoreCase = true) }?.uid
                }
                ArrayList(ids)
            }
            Button(

                onClick = {
                    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val cal1 = Calendar.getInstance().apply { time = fmt.parse(startDate)!! }
                    val cal2 = Calendar.getInstance().apply { time = fmt.parse(endDate)!! }
                    val startMonth = cal1.get(Calendar.MONTH) + 1
                    val endMonth   = cal2.get(Calendar.MONTH) + 1
                    val p = ProjectLayout(
                        name = name.capitalizeWords(),
                        desc = desc,
                        creator ="",
                        phase = "",
                        startMonth = startMonth,
                        endMonth = endMonth,
                        completedMilestones = 0f,
                        issues = arrayListOf(),
                        labels = arrayListOf(),
                        views = arrayListOf(),
                        users = assignmentIds,
                        startTS = startDate,
                        deadlineTS = endDate,
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
                Text("Create",color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    }
}