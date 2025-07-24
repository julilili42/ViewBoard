package com.example.viewboard.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueDeadlineFilter
import com.example.viewboard.components.homeScreen.ProgressCard
import com.example.viewboard.components.homeScreen.ProjectGrid
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.components.homeScreen.ProfileHeader
import com.example.viewboard.stateholder.IssueViewModel
import com.example.viewboard.stateholder.MainViewModel
import com.example.viewboard.stateholder.ViewsViewModel
import com.example.viewboard.ui.home.DraggableMyTasksSection
import com.example.viewboard.ui.navigation.BottomBarScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    activeProjects: List<String> = listOf("Created", "Shared"),
    viewModel: MainViewModel,
    issueViewModel: IssueViewModel,
    viewsViewModel: ViewsViewModel,
    modifier: Modifier,
    onSortTasks: () -> Unit = {}
) {

    var columnHeightPx    by remember { mutableStateOf(0) }
    var screenHeightPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val span by viewModel.timeSpan.collectAsState(
        initial = IssueDeadlineFilter.CURRENT_MONTH
    )
    val contactHeight = 300.dp
    val topBlockHeightPx = with(density) { contactHeight.toPx() }
    val progress by viewModel.progress.collectAsState()
    val percentCompleted = progress.percentComplete
    Scaffold(
        topBar = {
                    ProfileHeader(
                        name = AuthAPI.getCurrentDisplayName() ?: "failed to load username",
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
                .onGloballyPositioned { cords ->
                    screenHeightPx = cords.size.height
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(contactHeight )
                    .onSizeChanged { size ->
                        columnHeightPx = size.height
                    }
                    .onGloballyPositioned { cords ->
                        columnHeightPx = cords.size.height
                    }
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                ProjectGrid(
                    projects = activeProjects,
                    title = stringResource(R.string.ActiveProjects)
                ) { projectName ->
                    navController.navigate(Screen.ProjectScreen.createRoute(projectName))
                }
                Spacer(Modifier.height(24.dp))


                Log.d("IssueProgress", "Progress: total=${progress.totalIssues}, done=${progress.completedIssues}, percent=${progress.percentComplete},span=${span}")
                ProgressCard(
                    title = span,
                    progress = percentCompleted,
                    onClick={viewModel.advanceTimeSpan()}
                )
            }
            DraggableMyTasksSection(
                navController = navController,
                onSortClick = onSortTasks,
                issueViewModel = issueViewModel,
                viewsViewModel = viewsViewModel,
                modifier = Modifier.fillMaxSize(),
                minSheetHeightPx =  (screenHeightPx - topBlockHeightPx).coerceAtLeast(0f),
            )
        }
    }
}


