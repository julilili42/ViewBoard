package com.example.viewboard.frontend.components.project

import OptionsMenuButton
import androidx.compose.runtime.mutableStateOf
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale
import colorFromCode
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.frontend.components.home.issueProgress.IssueProgressCalculator
import generateProjectCodeFromDbId
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.navigation.NavController
import com.example.viewboard.backend.dataLayout.IssueDeadlineFilter
import com.example.viewboard.backend.dataLayout.IssueProgress


@Composable
fun ProjectItem(
    project: ProjectLayout,
    color: Color = Color.White,
    calculator: IssueProgressCalculator = remember { IssueProgressCalculator() },
    avatarUris: List<Uri>,
    editable: Boolean = true,
    navController: NavController,
    onClick: () -> Unit
) {
    val progress by produceState<IssueProgress>(
        initialValue = IssueProgress(0, 0, 0f),
        key1 = project.id,
        key2 = IssueDeadlineFilter.ALL_TIME
    ) {
        calculator
            .getProjectProgressFlow(project.id, IssueDeadlineFilter.ALL_TIME)
            .collect { value = it }
    }

    val start = project.startTS
    val end = project.deadlineTS
    val startLabel = labelFromIsoDate(start)
    val endLabel = labelFromIsoDate(end)
    val projectNameCode = generateProjectCodeFromDbId(project.id)
    val projectNamecolor = colorFromCode(projectNameCode)
    val avatarSize = 18.dp


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            projectNamecolor,
                            projectNamecolor.copy(alpha = 0.6f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .background(projectNamecolor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = projectNameCode,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    val scope = rememberCoroutineScope()
                    var showConfirmDialog by remember { mutableStateOf(false) }

                    if (showConfirmDialog) {
                        AlertDialog(
                            onDismissRequest = { showConfirmDialog = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    scope.launch {
                                        FirebaseAPI.rmProject(
                                            id = project.id,
                                            onSuccess = { /* TODO */ },
                                            onFailure = { /* TODO */ }
                                        )
                                    }
                                    showConfirmDialog = false
                                }) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showConfirmDialog = false }) {
                                    Text("Cancel")
                                }
                            },
                            title = { Text("Delete Project?") },
                            text = { Text("Do you really want to delete the project?") }
                        )
                    }
                    if (editable) {
                        OptionsMenuButton(
                            options = listOf(
                                "Edit" to {
                                    navController.navigate("project/edit/${project.id}")
                                },
                                "Delete" to {
                                    showConfirmDialog = true
                                }
                            ),
                            modifier = Modifier
                        )
                    }
                }
                Text(
                    text = "$startLabel – $endLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(Modifier.weight(1f))
                val totalParticipants = project.users.size
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(avatarSize),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Participants",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$totalParticipants",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Light),
                        color = Color.White
                    )

                    Spacer(Modifier.weight(1f))
                    LinearProgressIndicator(
                        progress = progress.completedIssues.toFloat() / progress.totalIssues.toFloat(),
                        modifier = Modifier
                            .width(80.dp)
                            .height(8.dp),
                        trackColor = Color.White.copy(alpha = 0.3f),
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun labelFromIsoDate(dateStr: String): String {
    val iso = dateStr.substringBefore('T').substringBefore(' ')
    val localDate = LocalDate.parse(iso)
    val formatter = DateTimeFormatter.ofPattern("LLL yy", Locale("de"))
    return localDate.format(formatter)
}

