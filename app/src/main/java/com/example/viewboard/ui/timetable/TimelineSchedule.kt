package com.example.viewboard.ui.timetable


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.navigation.NavController
import com.example.viewboard.stateholder.IssueViewModel
import com.example.viewboard.ui.utils.extractIssueDateTimes


@Composable
fun TimelineSchedule(
    year: Int,
    month: Int,
    height: Dp = 510.dp,
    selectedDate: LocalDate?,
    issueViewModel: IssueViewModel,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onselectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController

) {
    Box(modifier = modifier.height(height)) {
        val issuesList by issueViewModel.displayedAllIssues.collectAsState()
        val dummyIssues = extractIssueDateTimes(issuesList)
        Column(modifier = Modifier.fillMaxSize()) {
            MonthYearPicker(
                year = year,
                month = month,
                onMonthChange = onMonthChange,
                onYearChange = onYearChange,
                modifier = Modifier
                    .fillMaxWidth()
            )
            MonthCalendar(
                year = year,
                month = month,
                selectedDate = selectedDate,
                issues = dummyIssues,
                onDateSelected = onselectDate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}


