package com.example.viewboard.ui.timetable
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.viewboard.dataclass.Project
import java.time.LocalDate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import java.time.format.DateTimeFormatter
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.navigation.NavController
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.components.HomeScreen.ProjectCardTasks
import com.example.viewboard.ui.issue.IssueUiItem
import com.example.viewboard.ui.navigation.Screen
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun TimelineSchedule(
    year: Int,
    month: Int,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    projects: List<ProjectLayout>,
    phases: List<String>,
    issues: List<IssueLayout>,
    modifier: Modifier = Modifier,
    navController: NavController

) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }

    BoxWithConstraints(modifier = modifier) {
        val totalPx = constraints.maxWidth.toFloat()
        val monthPx = totalPx / 6f
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.getDefault())

        val matchingIssues = remember(selectedDate, issues) {
            issues.filter { issue ->
                runCatching {
                    // Aus dem Timestamp erst den Datums-String holen …
                    val dateString = issue.deadlineTS.getDate()
                    // … und diesen parsen
                    LocalDate.parse(dateString, formatter)
                }.getOrNull() == selectedDate
            }
        }
        val dummyIssues = extractIssueDates(issues)

        Column(modifier = Modifier.fillMaxSize()) {
            // 1) Monat/Jahr Picker
            MonthYearPicker(
                year = year,
                month = month,
                onMonthChange = onMonthChange,
                onYearChange = onYearChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // 2) Kalender
            MonthCalendar(
                year = year,
                month = month,
                selectedDate = selectedDate,
                issues = dummyIssues,
                onDateSelected = { selectedDate = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            // 3) Timeline-Reihen
            phases.forEach { phase ->
                PhaseRow(
                    projects = projects.filter { it.phase == phase },
                    monthPx = monthPx.toDp()
                )
            }

            // 4) Issues-Bereich
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // nimmt den restlichen Platz ein
                    .padding(start = 8.dp, top = 0.dp, end = 8.dp, bottom = 0.dp)
            ) {
                when {
                    selectedDate == null -> {
                        Text(
                            text = "Wähle ein Datum aus dem Kalender.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    matchingIssues.isEmpty() -> {
                        Text(
                            text = "Keine Issues an diesem Tag.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Text(
                                    text = "Issues für $selectedDate",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            items(matchingIssues.size) { index ->
                                val issue = matchingIssues[index]
                                val formatter = DateTimeFormatter.ISO_LOCAL_DATE
                                val dueDateTime = runCatching {
                                    // erst das Datum als String holen…
                                    val dateString = issue.deadlineTS.getDate()
                                    // …und dann als LocalDate parsen
                                    LocalDate.parse(dateString, formatter).atStartOfDay()
                                }.getOrElse { LocalDateTime.MIN }

                                ProjectCardTasks(
                                    name = issue.title,
                                    dueDateTime = dueDateTime,
                                    onClick = {
                                        navController.navigate(Screen.IssueScreen.createRoute(
                                            issue.title,
                                            "project.id"
                                        ))
                                    },
                                    onMenuClick = {
                                        navController.navigate(Screen.IssueScreen.createRoute(
                                            issue.title,
                                            "project.id"
                                        ))
                                    }
                                )
                            }
                        }
                    }
                }
            }
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
            val iso = issue.deadlineTS.export()
            val datePart = iso.substringBefore('T')
            LocalDate.parse(datePart, isoFormatter)
        }.getOrNull()
    }
}
