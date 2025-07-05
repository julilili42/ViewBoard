package com.example.viewboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.components.HomeScreen.ProgressCard
import com.example.viewboard.components.HomeScreen.ProjectGrid
import com.example.viewboard.ui.navigation.Screen
import java.time.LocalDateTime
import com.example.viewboard.components.HomeScreen.ProfileHeader
import com.example.viewboard.ui.navigation.BottomBarScreen

val tasks: List<Pair<String, LocalDateTime>> = listOf(
    "Issue 1" to LocalDateTime.now().plusDays(1),
    "Issue 2" to LocalDateTime.now().plusDays(1),
    "Issue 1" to LocalDateTime.now().plusDays(2),
    "Issue 2" to LocalDateTime.now().plusDays(2),
    "Issue 1" to LocalDateTime.now().plusDays(3),
    "Issue 2" to LocalDateTime.now().plusDays(3)
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    activeProjects: List<String> = listOf("Created", "Shared", "Archived", "All"),
    myTasks: List<Pair<String, LocalDateTime>> = tasks,
    modifier: Modifier,
    onSortTasks: () -> Unit = {}
) {
    var columnHeightPx    by remember { mutableStateOf(0) }
    var screenHeightPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val topBlockHeightPx = with(density) { 400.dp.toPx() }
    Scaffold(
        topBar = {
                    ProfileHeader(
                        name = AuthAPI.getDisplayName() ?: "failed to load username",
                        subtitle = "Welcome back!!",
                        navController =navController,
                        showBackButton = false ,
                        onProfileClick = {
                            navController.navigate(BottomBarScreen.Profile.route)
                        },
                        onBackClick = {navController.navigateUp()},
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .background(MaterialTheme.colorScheme.background)
                .onGloballyPositioned { coords ->
                    screenHeightPx = coords.size.height
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(400.dp)
                    .onSizeChanged { size ->
                        columnHeightPx = size.height
                    }
                    .onGloballyPositioned { coords ->
                        columnHeightPx = coords.size.height
                    }
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                ProjectGrid(
                    projects = activeProjects,
                    title = stringResource(R.string.ActiveProjects)
                ) { projectName ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectName))
                }
                Spacer(Modifier.height(24.dp))
                val completedTasks = 5
                val totalTasks = myTasks.size.coerceAtLeast(1)
                ProgressCard(
                    title = "Weekly Targets",
                    progress = completedTasks / totalTasks.toFloat()
                )

            }
            DraggableMyTasksSection(
                navController = navController,
                myTasks = myTasks,
                onSortClick = onSortTasks,
                modifier = Modifier.fillMaxSize(),
                minSheetHeightPx =  (screenHeightPx - topBlockHeightPx).coerceAtLeast(0f),
            )
        }
    }
}

@Composable
fun DraggableMyTasksSection(
    navController: NavController,
    myTasks: List<Pair<String, LocalDateTime>>,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
    minSheetHeightPx: Float = 0f
) {
    val density = LocalDensity.current
    var currentSheetHeightPx by remember { mutableStateOf(0f) }
    BoxWithConstraints(modifier = modifier) {
        val maxHeightPx = with(density) { maxHeight.toPx() }

        if (currentSheetHeightPx == 0f && maxHeightPx > 0f) {
            currentSheetHeightPx = minSheetHeightPx.coerceAtMost(maxHeightPx)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { currentSheetHeightPx.toDp() })
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .align(Alignment.BottomCenter)
                .background(Color.White)
        ) {
            // Ziehgriff
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .pointerInput(Unit) {}
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            val newHeight = currentSheetHeightPx - delta
                            currentSheetHeightPx = newHeight.coerceIn(minSheetHeightPx, maxHeightPx * 0.8f) // Max 80%
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), shape = MaterialTheme.shapes.small)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.my_tasks),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    val onFirstClick = fun(){}
                    IconButton(
                        onClick = onFirstClick,
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.shapes.small
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sort_desc_svgrepo_com),
                            contentDescription = stringResource(R.string.sort_desc_svgrepo_com),
                            modifier = Modifier.fillMaxSize(1f),
                            tint = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                    val onSecondClick = fun(){}
                    IconButton(
                        onClick = onSecondClick,
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.filter_svgrepo_com__1),
                            contentDescription = stringResource(R.string.filter_svgrepo_com__1),
                            modifier = Modifier
                                .padding(0.dp),
                            tint = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                }
            }
            MyTasksScreen(
                navController = navController,
                myTasks = myTasks,
                onSortClick = onSortClick,
            )
        }
    }
}