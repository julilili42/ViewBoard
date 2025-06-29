package com.example.viewboard.components.HomeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.example.viewboard.components.HomeScreen.ProjectCardTasks
import java.time.LocalDateTime
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip

import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.viewboard.R

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
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            // Nur die oberen Ecken runden, unten bleiben eckig
            //.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ,
        containerColor = Color.White,
        /*topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Tasks",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(onClick = onSortClick) {
                        Icon(Icons.Default.Star, contentDescription = "Sort Tasks")
                    }
                    IconButton(onClick = onSortClick) {
                        Icon(
                            painter = painterResource(R.drawable.filter_svgrepo_com),
                            contentDescription = "Filter Tasks",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
        }*/
    )  { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()


        ) {
            // Fading vertical grid of tasks
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
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
                                onClick = { navController.navigate("taskDetail/$name") },
                                onMenuClick = { /* TODO: menu logic */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
