package com.example.viewboard.ui.timetable
import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.viewboard.dataclass.Project

@Composable
fun TimelineSchedule(
    projects: List<Project>,
    phases: List<String>,
    modifier: Modifier = Modifier
) {
   /* BoxWithConstraints(modifier = modifier) {
        // gesamte Breite in Px
        val totalPx = constraints.maxWidth.toFloat()
        // je Monat eine Spaltenbreite
        val monthPx = totalPx / 6f

        Column {
            MonthHeader(monthPx)
            /*phases.forEach { phase ->
                PhaseRow(
                    projects = projects.filter { it.phase == phase },
                    monthPx = monthPx
                )
            }*/
        }
    }*/
    BoxWithConstraints(modifier = modifier) {
        val totalPx = constraints.maxWidth.toFloat()
        val monthPx = totalPx / 6f

        Column {
            // Kalender für Juni 2025 (Beispiel)
            MonthCalendar(
                year  = 2025,
                month = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(8.dp)
            )
        }
    }
}


// Hilfs­extension um Px → Dp zu konvertieren
fun Float.toDp(): Dp =
    (this / Resources.getSystem().displayMetrics.density).dp
