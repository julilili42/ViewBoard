package com.example.viewboard.frontend.screens.timetable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.viewboard.frontend.components.home.profile.ProfileHeader
import com.example.viewboard.frontend.navigation.BottomBarScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.viewboard.frontend.components.timetable.SegmentedSwitch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.example.viewboard.frontend.stateholder.IssueViewModel
import com.example.viewboard.frontend.stateholder.ProjectViewModel
import com.example.viewboard.frontend.components.timetable.DraggableMyIssuesSection
import com.example.viewboard.frontend.components.timetable.TimelineSchedule
import com.example.viewboard.frontend.components.timetable.VerticalTimelineSchedule
import com.example.viewboard.frontend.components.views.EdgeToEdgeRoundedRightItemWithBadge
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    navController: NavHostController,
    issueViewModel: IssueViewModel,
    projectViewModel: ProjectViewModel,
) {
    val configuration = LocalConfiguration.current
    var contactHeight by remember { mutableStateOf(configuration.screenHeightDp.dp) }
    val density = LocalDensity.current
    val projects by projectViewModel.displayedViewProjects.collectAsState()
    val topBlockHeightPx = with(density) { contactHeight.toPx() }
    var screenHeightPx by remember { mutableIntStateOf(0) }
    val issues by issueViewModel.displayedAllIssues.collectAsState()
    var showProjects by remember { mutableStateOf(true) }
    val today = LocalDate.now()
    var year by remember { mutableIntStateOf(today.year) }
    var month by remember { mutableIntStateOf(today.monthValue) } // 1..12
    var selectedDate by remember { mutableStateOf<LocalDate?>(today) }
    Scaffold(
        topBar = {

            ProfileHeader(
                name = "Raoul",
                subtitle = "Just a short overview for you.",
                navController = navController,
                showBackButton = false,
                onProfileClick = {
                    navController.navigate(BottomBarScreen.Profile.route)
                },
                onBackClick = { navController.navigateUp() }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding() + 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .onGloballyPositioned { coords ->
                    screenHeightPx = coords.size.height
                },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                EdgeToEdgeRoundedRightItemWithBadge(
                    viewName = "Timetable",
                    modifier = Modifier.padding(start = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SegmentedSwitch(
                        options = "Projects" to "Issues",
                        selectedLeft = showProjects,
                        onSelectionChange = { showProjects = it }
                    )
                }
                Box(
                    modifier = Modifier
                        //.weight(1f)
                        .weight(0.75f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color.White)
                ) {
                    if (showProjects) {
                        contactHeight = configuration.screenHeightDp.dp
                        VerticalTimelineSchedule(
                            projects = projects,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        contactHeight = 530.dp
                        selectedDate?.let {
                            TimelineSchedule(
                                year = year,
                                month = month,
                                onYearChange = { year = it },
                                onMonthChange = { month = it },
                                issueViewModel = issueViewModel,
                                height = contactHeight,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(contactHeight),
                                onselectDate = { selectedDate = it },
                                selectedDate = selectedDate,
                                navController = navController

                            )
                        }
                    }
                }
            }
        }
        if (!showProjects) {
            DraggableMyIssuesSection(
                navController = navController,
                onSortClick = { },
                issues = issues,
                selectedDate = selectedDate,
                modifier = Modifier.fillMaxSize(),
                minSheetHeightPx = (screenHeightPx - topBlockHeightPx).coerceAtLeast(0f),
            )
        }
    }
}



