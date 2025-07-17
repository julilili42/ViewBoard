package com.example.viewboard.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.viewboard.R
import com.example.viewboard.components.homeScreen.ProfileHeader
import com.example.viewboard.ui.navigation.BottomBarScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.viewboard.components.timetable.SegmentedSwitch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.timetable.CustomIcon
import com.example.viewboard.ui.timetable.TimelineSchedule
import com.example.viewboard.ui.timetable.VerticalTimelineSchedule
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(navController: NavHostController) {
    val projectLayouts = remember { mutableStateListOf<ProjectLayout>() }
    val issuesList = remember { mutableStateListOf<IssueLayout>() }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            FirebaseAPI.getProjectsFromUser(AuthAPI.getUid()).collect { layouts ->
                projectLayouts.clear()
                projectLayouts.addAll(layouts)
            }
        } catch (e: Exception) {
            error = "Projekte-Ladefehler: ${e.localizedMessage}"
        }
    }

    LaunchedEffect(Unit) {
        try {
            FirebaseAPI.getAllIssues(/* hier ggf. projectId oder leer für alle */).collect { list ->
                issuesList.clear()
                issuesList.addAll(list)
            }
        } catch (e: Exception) {
            error = "Issues-Ladefehler: ${e.localizedMessage}"
        }
    }
    var showProjects by remember { mutableStateOf(true) }
    val today = LocalDate.now()
    var year by remember { mutableStateOf(today.year) }
    var month by remember { mutableStateOf(today.monthValue) } // 1..12
    var selectedDate by remember { mutableStateOf<LocalDate?>(today) }
    Scaffold(
        topBar = {

            ProfileHeader(
                name = "Raoul",
                subtitle = "Just a short overview for you.",
                navController =navController,
                showBackButton = false,
                onProfileClick = {
                    navController.navigate(BottomBarScreen.Profile.route)
                },
                onBackClick = {navController.navigateUp()}
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.padding(innerPadding)
                .padding(top = innerPadding.calculateTopPadding())
            // .background(Color.White) // sicherheitshalber
        ) {
            Text(
                text ="Timetable", // z.B. "My Projects"
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
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
                Row {
                    CustomIcon(
                        iconRes = R.drawable.sort_desc_svgrepo_com,
                        contentDesc = stringResource(R.string.sort_desc_svgrepo_com),
                        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
                        iconTint = Color.White, // oder MaterialTheme.colorScheme.primary
                        width = 40.dp,
                        height = 40.dp,
                        onClick = { /* sort action */ },
                        modifier = Modifier

                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CustomIcon(
                        iconRes = R.drawable.filter_svgrepo_com__1,
                        contentDesc = stringResource(R.string.filter_svgrepo_com__1),
                        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 1f),
                        iconTint = Color.White,
                        width = 40.dp,
                        height = 40.dp,
                        onClick = { /* filter action */ },
                        modifier = Modifier

                    )
                }
            }

            Box(
                modifier = Modifier
                    //.weight(1f)
                    .weight(0.75f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
            ) {
                if (showProjects) {
                    VerticalTimelineSchedule(
                        projects = projectLayouts,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    selectedDate?.let {
                        TimelineSchedule(
                            year         = year,
                            month        = month,
                            onYearChange = { year = it },
                            onMonthChange= { month = it },
                            projects     = projectLayouts,
                            phases       = listOf(/* … */),
                            issues       = issuesList,
                            modifier     = Modifier.fillMaxSize(),
                            navController = navController

                        )
                    }
                }
            }
        }
    }
}



