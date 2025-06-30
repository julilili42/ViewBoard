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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*

@Composable
fun TimelineSchedule(
    year: Int,
    month: Int,                            // 1..12
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    projects: List<Project>,
    phases: List<String>,
    modifier: Modifier = Modifier
) {
    // Heutiges Datum, um z.B. Current Month oder Today hervorzuheben
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    BoxWithConstraints(modifier = modifier) {
        val totalPx = constraints.maxWidth.toFloat()
        val monthPx = totalPx / 6f

        Column {
            // 1) Header mit Monat/Jahr-Steuerung
            MonthYearPicker(
                year = year,
                month = month,
                onMonthChange = { newMonth ->
                    onMonthChange(newMonth)
                },
                onYearChange = { newYear ->
                    onYearChange(newYear)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // 2) Dein Kalender (oder Timeline) für den gewählten Monat
            MonthCalendar(
                year    = year,
                month   = month, // falls MonthCalendar 0-basiert ist
                selectedDate  = selectedDate,  // oder manage Selection hier oder außen
                onDateSelected = { date ->
                    selectedDate = date
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(8.dp)
            )

            // 3) Hier kannst Du danach deine Phasen/Timeline-Reihen rendern,
            //    jeweils mit Breite monthPx.toDp()
            phases.forEach { phase ->
                PhaseRow(
                    projects = projects.filter { it.phase == phase },
                    monthPx  = monthPx.toDp()
                )
            }
        }
    }
}

@Composable
fun PhaseRow(
    projects: List<Project>,
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
                        color = project.color,
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
