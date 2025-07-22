package com.example.viewboard.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.components.homeScreen.CustomDropdownMenu
import com.example.viewboard.components.homeScreen.ProgressCard
import com.example.viewboard.components.homeScreen.ProjectGrid
import com.example.viewboard.ui.navigation.Screen
import java.time.LocalDateTime
import com.example.viewboard.components.homeScreen.ProfileHeader
import com.example.viewboard.components.homeScreen.TimeSpanFilter
import com.example.viewboard.components.homeScreen.ViewSelectorDropdown
import com.example.viewboard.ui.issue.IssueViewModel
import com.example.viewboard.ui.issue.MainViewModel
import com.example.viewboard.ui.issue.ProjectViewModel
import com.example.viewboard.ui.issue.ViewsViewModel
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
    activeProjects: List<String> = listOf("Created", "Shared"),//, "Archived", "All"
    myTasks: List<Pair<String, LocalDateTime>> = tasks,
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
        initial = TimeSpanFilter.CURRENT_MONTH
    )
    val contactheight = 300.dp
    val topBlockHeightPx = with(density) { contactheight.toPx() }
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
                .onGloballyPositioned { coords ->
                    screenHeightPx = coords.size.height
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .height(contactheight )
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
                    navController.navigate(Screen.ProjectScreen.createRoute(projectName))
                }
                Spacer(Modifier.height(24.dp))
                val completedTasks = 5
                val totalTasks = myTasks.size.coerceAtLeast(1)
                val progress by viewModel.progress.collectAsState()
                val percentCompleted = progress.percentComplete

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

@Composable
fun DraggableMyTasksSection(
    navController: NavController,
    onSortClick: () -> Unit,
    issueViewModel: IssueViewModel ,
    viewsViewModel: ViewsViewModel ,
    modifier: Modifier = Modifier,
    minSheetHeightPx: Float = 0f
) {
    val density = LocalDensity.current
    var currentSheetHeightPx by remember { mutableStateOf(0f) }
    // State, um die Auswahl ggf. weiterzuverwenden
    val viewLayouts by viewsViewModel.displayedViewsHome.collectAsState()
    val selectedViewId by viewsViewModel.selectedViewId.collectAsState()
    val selectedName by viewsViewModel.selectedViewName.collectAsState()



    Log.d("selectedId", "selectedName =$selectedViewId")


    selectedViewId?.let { issueViewModel.setCurrentViewId(it) }
    selectedViewId?.let { issueViewModel.loadIssuesFromView(selectedViewId!!) }
    LaunchedEffect(selectedViewId) {
        selectedViewId?.let { issueViewModel.setCurrentViewId(it) }
        selectedViewId?.let { issueViewModel.loadIssuesFromView(it) }
    }
    val issues by issueViewModel.displayedIssuesFromViews.collectAsState()




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
                    CustomDropdownMenu(
                        options = viewLayouts,
                        selectedOption = selectedName,
                        onOptionSelected = {view ->
                            Log.d("selectedName", "viewid =$view ")
                                viewsViewModel.selectView(view)},
                        modifier = Modifier
                            .fillMaxWidth(0.4f)   // nur 80% der Breite
                            .padding()
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Spacer(modifier = Modifier.width(15.dp))
                }
            }
            MyTasksScreen(
                navController = navController,
                issues = issues,
                onSortClick = onSortClick,
            )
        }
    }
}

fun TimeSpanFilter.next(): TimeSpanFilter = when (this) {
    TimeSpanFilter.CURRENT_YEAR  -> TimeSpanFilter.CURRENT_MONTH
    TimeSpanFilter.CURRENT_MONTH -> TimeSpanFilter.CURRENT_WEEK
    TimeSpanFilter.CURRENT_WEEK  -> TimeSpanFilter.CURRENT_YEAR
    TimeSpanFilter.ALL_TIME -> TimeSpanFilter.ALL_TIME
}