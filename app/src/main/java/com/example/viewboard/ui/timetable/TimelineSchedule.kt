package com.example.viewboard.ui.timetable

import android.content.res.Resources
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import java.time.format.DateTimeFormatter
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.Timestamp
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.components.homeScreen.CustomDropdownMenu
import com.example.viewboard.components.homeScreen.ProjectCardTasks
import com.example.viewboard.components.homeScreen.TimeSpanFilter
import com.example.viewboard.ui.issue.IssueViewModel
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.screens.DraggableMyTasksSection
import com.example.viewboard.ui.screens.MyTasksScreen
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.Locale

@Composable
fun TimelineSchedule(
    year: Int,
    month: Int,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onselectDate: (LocalDate) -> Unit,
    selectedDate: LocalDate?,
    projects: List<ProjectLayout>,
    phases: List<String>,
    issueViewModel: IssueViewModel,
    modifier: Modifier = Modifier,
    height: Dp = 510.dp,
    navController: NavController

) {


    BoxWithConstraints(modifier = modifier.height(height)) {
        var columnHeightPx    by remember { mutableStateOf(0) }
        var screenHeightPx by remember { mutableStateOf(0) }
        val density = LocalDensity.current

        val totalPx = constraints.maxWidth.toFloat()
        val monthPx = totalPx / 6f
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.getDefault())
        val issuesList by issueViewModel.displayedAllIssues.collectAsState()

        val dummyIssues = extractIssueDateTimes(issuesList)

        Column(modifier = Modifier.fillMaxSize()) {
            // 1) Monat/Jahr Picker
            MonthYearPicker(
                year = year,
                month = month,
                onMonthChange = onMonthChange,
                onYearChange = onYearChange,
                modifier = Modifier
                    .fillMaxWidth()
            )

            // 2) Kalender
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



@Composable
fun PhaseRow(
    projects: List<ProjectLayout>,
    monthPx: Dp,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        projects.forEach { project ->
            // Breite: Anzahl Monate * Spalten-Breite
            val span = (project.endMonth - project.startMonth + 1).coerceAtLeast(1)
            Box(
                modifier = Modifier
                    .width(monthPx * span)
                    .height(24.dp)
                    .background(
                        color = Color(0xFFFF5722),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
// Hilfs­extension um Px → Dp zu konvertieren
fun Float.toDp(): Dp =
    (this / Resources.getSystem().displayMetrics.density).dp

fun extractIssueDates(issues: List<IssueLayout>): List<LocalDate> {
    val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    return issues.mapNotNull { issue ->
        runCatching {
            // z.B. "2025-07-02T14:35:00Z" → "2025-07-02"
            val iso = issue.deadlineTS
            val datePart = iso.substringBefore('T')
            LocalDate.parse(datePart, isoFormatter)
        }.getOrNull()
    }
}
fun extractIssueDateTimes(issues: List<IssueLayout>): List<OffsetDateTime> {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    return issues.mapNotNull { issue ->
        runCatching {
            OffsetDateTime.parse(issue.deadlineTS, formatter)
        }.getOrNull()
    }
}
@Composable
fun DraggableMyIssuesSection(
    navController: NavController,
    onSortClick: () -> Unit,
    issues: List<IssueLayout>,
    selectedDate: LocalDate? = null,
    modifier: Modifier = Modifier,
    minSheetHeightPx: Float = 0f
) {

    val density = LocalDensity.current
    var currentSheetHeightPx by remember { mutableStateOf(0f) }
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.getDefault())
    // State, um die Auswahl ggf. weiterzuverwenden
    val matchingIssues = remember(selectedDate, issues) {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        issues.filter { issue ->
            runCatching {
                // 1) Parse den ISO‑Timestamp zu einem OffsetDateTime
                val odt = OffsetDateTime.parse(issue.deadlineTS, formatter)
                // 2) Extrahiere das lokale Datum (ohne Zeit)
                odt.toLocalDate()
            }.getOrNull() == selectedDate
        }
    }

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
                    .height(30.dp)
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
                        .padding(bottom = 8.dp, start = 16.dp, end = 16.dp  ),//horizontal = 16.dp, vertical = 8.dp
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    //Spacer(modifier = Modifier.width(15.dp))
                }
            }
            MyTasksScreen(
                navController = navController,
                issues = matchingIssues,
                onSortClick = onSortClick,
            )
        }
    }
}