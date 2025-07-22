package com.example.viewboard.ui.screens



import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.ui.Alignment

import kotlinx.coroutines.launch
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.issue.IssueViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel

import kotlinx.coroutines.launch

import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectIssueDialog(
    viewId: String,
    projects: List<ProjectLayout>,
    loadIssuesForProject: (String) -> Unit = {},

    issueViewModel: IssueViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    var showProjectList by remember { mutableStateOf(true) }
    var selectedProject by remember { mutableStateOf<ProjectLayout?>(null) }
    val baseColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f)
    var columnWidthPx by remember { mutableStateOf(0f) }
    issueViewModel.setCurrentViewId(viewId)
    val issues by issueViewModel.issuesForSelectedProject.collectAsState()
    val filterIssues by issueViewModel.allIssues.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val displayed = remember(issues, filterIssues) {
        issues.filterNot { issue ->
            filterIssues.any { it.id == issue.id }
        }
    }
    val filterIds = remember(filterIssues) {
        filterIssues.map { it.id }.toSet()
    }
    // Nur Projekte mit mindestens einem Issue anzeigen
    val projectsWithIssues = remember(projects, filterIds) {
        projects.filter { project ->
            // 1) Projekt muss überhaupt Issues haben
            project.issues.isNotEmpty()

                    && project.issues.any { issue ->
                issue !in filterIds
            }
        }
    }
    LaunchedEffect(displayed) {
        if (displayed.isEmpty()) {
            showProjectList = true
            selectedProject = null
        }
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.heightIn(max = 400.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            if (showProjectList) {
                Text("Projects")
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(selectedProject?.name.orEmpty())
                    IconButton(onClick = {
                        showProjectList = true
                        selectedProject = null
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            }
        },
        text = {
            if (showProjectList) {
                LazyColumn(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        columnWidthPx = coords.size.width.toFloat()
                        Log.d("ProjectIssueDialog", "Width: $columnWidthPx px")
                    }
                ) {
                    val gradient = Brush.horizontalGradient(
                        colors = listOf(
                            baseColor.copy(alpha = 0.2f),
                            baseColor,
                            baseColor.copy(alpha = 0.2f)
                        ),
                        startX = 0f,
                        endX = columnWidthPx
                    )
                    items(projectsWithIssues ) { project ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    val strokeWidth = 1.dp.toPx()
                                    drawLine(
                                        brush = gradient,
                                        start = Offset(0f, size.height),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = strokeWidth
                                    )
                                }
                        ) {
                            ListItem(
                                modifier = Modifier
                                    .clickable {
                                        selectedProject = project
                                        showProjectList = false
                                        issueViewModel.selectProject(project.id)
                                        issueViewModel.loadIssuesForProject()
                                    }
                                    .padding(vertical = 8.dp),
                                headlineContent = { Text(project.name) },
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.onGloballyPositioned { coords ->
                        columnWidthPx = coords.size.width.toFloat()
                        Log.d("ProjectIssueDialog", "Width: $columnWidthPx px")
                    }
                ) {
                    val gradient = Brush.horizontalGradient(
                        colors = listOf(
                            baseColor.copy(alpha = 0.2f),
                            baseColor,
                            baseColor.copy(alpha = 0.2f)
                        )
                    )
                    items(displayed) { issue ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    val strokeWidth = 1.dp.toPx()
                                    drawLine(
                                        brush = gradient,
                                        start = Offset(0f, size.height),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = strokeWidth
                                    )
                                }
                        ) {
                            ListItem(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            FirebaseAPI.addIssueToView(
                                                viewID = viewId,
                                                id = issue.id
                                            )
                                        }
                                    },
                                headlineContent = { Text(issue.title) },
                                colors = ListItemDefaults.colors(

                                    headlineColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        },
        dismissButton = {},
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}

