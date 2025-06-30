package com.example.viewboard.ui.screens


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.viewboard.dataclass.Project
import androidx.compose.ui.graphics.Color


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

import com.example.viewboard.R
import com.example.viewboard.components.HomeScreen.ProfileHeader
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.viewboard.components.Timetable.SegmentedSwitch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import com.example.viewboard.ui.timetable.CustomIcon
import com.example.viewboard.ui.timetable.TimelineSchedule
import com.example.viewboard.ui.timetable.VerticalTimelineSchedule
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(navController: NavHostController) {
    var showProjects by remember { mutableStateOf(true) }
    val today = LocalDate.now()
    var year by remember { mutableStateOf(today.year) }
    var month by remember { mutableStateOf(today.monthValue) } // 1..12
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    // Beispieldaten
    val projects = listOf(
        Project("A", "Desc A", "#A13", 1, 3, Color(0xFF00BCD4),5,2.53f),
        Project("B", "Desc B", "#B13", 4, 6, Color(0xFF8BC34A),4,1.53f),
        Project("C", "Desc C", "#D13", 3, 9, Color(0xFF8BC34A),4,3.53f),
        Project("D", "Desc D", "#G13", 2, 5, Color(0xFF8BC34A),4,1.53f),
        Project("E", "Desc E", "#F13", 2, 5, Color(0xFF8BC34A),4,3.53f),
        Project("F", "Desc F", "#M13", 4, 5, Color(0xFF8BC34A),4,1.53f),
        Project("G", "Desc G", "#N13", 8, 12, Color(0xFF8BC34A),4,1.53f),
    )
    val issues = listOf(
        Project("Issue 1", "Fix crash", "Backlog", 2, 2, Color(0xFFFF5722),4,1.53f),
        Project("Issue 2", "Add tests", "Backlog", 3, 4, Color(0xFFFFC107),5,4.53f)
    )

    Scaffold(
        topBar = {

                    ProfileHeader(
                        name = "Raoul",
                        subtitle = "Just a short overview for you.",
                        onProfileClick = {
                            navController.navigate(BottomBarScreen.Profile.route)
                        },
                        onBackClick = {navController.navigateUp()}
                    )
        },
        //containerColor = Color.White // sorgt dafür, dass Scaffold-Hintergrund weiß ist
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
                // Switch ganz links
                SegmentedSwitch(
                    options = "Projects" to "Issues",
                    selectedLeft = showProjects,
                    onSelectionChange = { showProjects = it }
                )

                // Icons ganz rechts
                Row {
                    /*IconButton(
                        onClick = { /* sort */ },
                        modifier = Modifier
                            .size(14.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            )

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sort_desc_svgrepo_com),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }*/
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
                    /*IconButton(
                        onClick = { /* filter */ },
                        modifier = Modifier
                            .size(14.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            )

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.filter_svgrepo_com__1),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .fillMaxSize()
                               ,
                            tint =  Color.White
                        )
                    }*/
                }
            }



            // Content-Bereich: weißen Hintergrund bereits gesetzt
            Box(
                modifier = Modifier
                    //.weight(1f)
                    .weight(0.75f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color.White)
            ) {
                if (showProjects) {
                    VerticalTimelineSchedule(
                        projects = projects,
                        phases   = listOf("#A13", "#B13","#D13","#G13","#F13","#M13","#N13"),
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    TimelineSchedule(
                        year         = year,
                        month        = month,
                        onYearChange = { year = it },
                        onMonthChange= { month = it },
                        projects     = issues,
                        phases       = listOf(/* … */),
                        modifier     = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}



