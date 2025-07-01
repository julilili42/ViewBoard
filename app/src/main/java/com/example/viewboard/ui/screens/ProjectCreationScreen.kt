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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import java.text.SimpleDateFormat
import java.util.Locale


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCreationScreen(
    navController: NavController,
    onCreate: () -> Unit = {}
) {
    val uiColor = uiColor()
    val scroll = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var totalMilestones by remember { mutableStateOf(0) }

    // Validierungen
    val datePattern = Regex("\\d{4}-\\d{2}-\\d{2}")
    val isNameValid by derivedStateOf { name.isNotBlank() }
    val isDescValid by derivedStateOf { desc.isNotBlank() }
    val isStartDateValid by derivedStateOf { datePattern.matches(startDate) }
    val isEndDateValid by derivedStateOf { datePattern.matches(endDate) }
    val isRangeValid by derivedStateOf {
        if (!isStartDateValid || !isEndDateValid) false
        else try {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            fmt.parse(startDate)!!.time <= fmt.parse(endDate)!!.time
        } catch (_: Exception) { false }
    }
    val isTotalMilestonesValid by derivedStateOf { totalMilestones > 0 }
    val allValid by derivedStateOf {
        isNameValid && isDescValid &&
                isStartDateValid && isEndDateValid && isRangeValid &&
                isTotalMilestonesValid
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
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
                singleLine = true,
                isError = !isNameValid && name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            if (!isNameValid && name.isNotBlank()) {
                Text("Name darf nicht leer sein", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // Beschreibung
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Beschreibung") },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp),
                maxLines = Int.MAX_VALUE,
                isError = !isDescValid && desc.isNotBlank()
            )
            if (!isDescValid && desc.isNotBlank()) {
                Text("Beschreibung darf nicht leer sein", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // Startdatum
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("YYYY-MM-DD") },
                singleLine = true,
                isError = !isStartDateValid && startDate.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            if (!isStartDateValid && startDate.isNotBlank()) {
                Text("Ungültiges Startdatum", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // Enddatum
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("YYYY-MM-DD") },
                singleLine = true,
                isError = (!isEndDateValid && endDate.isNotBlank()) ||
                        (isStartDateValid && isEndDateValid && !isRangeValid),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )
            when {
                !isEndDateValid && endDate.isNotBlank() ->
                    Text("Ungültiges Enddatum", color = MaterialTheme.colorScheme.error)
                isStartDateValid && isEndDateValid && !isRangeValid ->
                    Text("Enddatum muss nach Startdatum sein", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))

            // Meilensteine hochzählen
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Meilensteine: $totalMilestones", modifier = Modifier.weight(1f))
                IconButton(onClick = { totalMilestones++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Meilenstein hinzufügen")
                }
            }
            if (!isTotalMilestonesValid) {
                Text("Mindestens ein Meilenstein erforderlich", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))

            // Create Button
            Button(
                onClick = {
                    onCreate()
                    navController.popBackStack()
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

