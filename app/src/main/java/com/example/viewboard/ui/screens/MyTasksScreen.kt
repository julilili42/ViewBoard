package com.example.viewboard.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.time.LocalDateTime
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.components.homeScreen.ProjectCardTasks
import com.example.viewboard.ui.navigation.Screen

/**
 * Screen displaying "My Tasks" with a sort button and fade-edge effect.
 *
 * @param navController Navigation controller for screen transitions
 * @param myTasks List of task name and due date-time pairs
 * @param onSortClick Callback for sort/filter button
 */
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksScreen(
    navController: NavController,
    issues: List<IssueLayout>,
    onSortClick: () -> Unit = {}
) {
    // Root-Container ohne Scaffold
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(issues) { issue ->
                var dismissed by remember { mutableStateOf(false) }
                if (!dismissed) {
                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (dragAmount < -100) dismissed = true
                                }
                            }
                    ) {
                        val cleanId = issue.projectid.trim('{', '}')
                        var project by remember { mutableStateOf<ProjectLayout?>(null) }
                        LaunchedEffect(issue.projectid) {
                            try {
                            project = FirebaseAPI.getProject(cleanId)
                                Log.d("IssueWithProject", "Loaded project for issue ${issue.projectid}: $project")
                            } catch (e: Exception) {
                                Log.e("IssueWithProject", "Failed to load project ${issue.projectid}", e)
                            }// suspend call
                        }

                        project?.let {
                            ProjectCardTasks(
                                name = it.name,
                                dueDate = it.deadlineTS,
                                onClick = { navController.navigate(Screen.IssueScreen.createRoute(
                                    it.name,
                                    cleanId))
                                },
                                onMenuClick = {
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
