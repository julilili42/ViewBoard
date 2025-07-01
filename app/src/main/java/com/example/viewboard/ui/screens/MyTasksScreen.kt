package com.example.viewboard.ui.screens

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
import com.example.viewboard.components.HomeScreen.ProjectCardTasks
import com.example.viewboard.ui.navigation.Screen

/**
 * Screen displaying "My Tasks" with a sort button and fade-edge effect.
 *
 * @param navController Navigation controller for screen transitions
 * @param myTasks List of task name and due date-time pairs
 * @param onSortClick Callback for sort/filter button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksScreen(
    navController: NavController,
    myTasks: List<Pair<String, LocalDateTime>>,
    onSortClick: () -> Unit = {}
) {
    // Root-Container ohne Scaffold
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Optional: eigener TopBar-Bereich
        /*Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "My Tasks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            IconButton(onClick = onSortClick) {
                Icon(Icons.Default.Star, contentDescription = "Sort Tasks")
            }
        }*/

        // Grid der Tasks, beginnt exakt oben im Box
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(myTasks) { (name, dueDateTime) ->
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
                        ProjectCardTasks(
                            name = name,
                            dueDateTime = dueDateTime,
                            onClick = { navController.navigate(Screen.IssueCreationScreen.createRoute(name)) },
                            onMenuClick = {navController.navigate(Screen.IssueCreationScreen.createRoute(name))}
                        )
                    }
                }
            }
        }
    }
}
