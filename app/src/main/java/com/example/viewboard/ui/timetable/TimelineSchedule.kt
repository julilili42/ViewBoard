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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.unit.toSize

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

@Composable
fun MonthHeader(monthPx: Float) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        listOf(
            "Mon","Tue","Wed","Thu","Fri","Sat","Sun"
        ).forEach { m ->
            Box(
                Modifier
                    .width(monthPx.toDp())
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(m, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PhaseRow(
    projects: List<Project>,
    monthPx: Float
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(56.dp)    // etwas mehr Platz für Text + Progress
    ) {
        projects.forEach { p ->
            // Zeiten in Dp umrechnen
            val startDp = ((p.startMonth - 1) * monthPx).toDp()
            val widthDp = ((p.endMonth - p.startMonth + 1) * monthPx).toDp()

            Box(
                modifier = Modifier
                    .offset(x = startDp)
                    .width(widthDp)
                    .fillMaxHeight()

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                   // Text(p.name, style = MaterialTheme.typography.bodySmall)
                   // Text(p.description, style = MaterialTheme.typography.labelSmall)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Milestone-Bar, exakt so breit wie dieser Box-Container
                    MilestoneBar(
                        total     = p.totalMilestones,
                        completed = p.completedMilestones,
                        colors    = blueGradient,          // deine Farb-Liste
                        modifier  = Modifier.fillMaxWidth(),  // füllt die Box-Breite
                        height    = 8.dp,
                        spacing   = 2.dp,
                        corner    = 4.dp
                    )
                }
            }
        }
    }
}

val blueGradient = listOf(
    Color(0xFF0D47A1),
    Color(0xFF1565C0),
    Color(0xFF1976D2),
    Color(0xFF1E88E5),
    Color(0xFF42A5F5)
)

@Composable
fun MilestoneBar(
    total: Int,
    completed: Float,          // jetzt Float statt Int
    colors: List<Color>,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    spacing: Dp = 2.dp,
    corner: Dp = 4.dp
) {
    Row(
        modifier = modifier
            .height(height),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(total) { idx ->
            // Füll-Bruchteil für dieses Segment
            val fillFraction = (completed - idx).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(corner))
                    .background(Color.LightGray.copy(alpha = 0.3f)),
            ) {
                // Nur zeichnen, wenn fillFraction > 0
                if (fillFraction > 0f) {
                    // Farbwahl aus Gradient oder per idx
                    val segmentColor = colors.getOrElse(idx) { colors.last() }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fillFraction)   // nur Teil der Breite
                            .clip(RoundedCornerShape(corner))
                            .background(segmentColor)
                    )
                }
            }
        }
    }
}



// Hilfs­extension um Px → Dp zu konvertieren
fun Float.toDp(): Dp =
    (this / Resources.getSystem().displayMetrics.density).dp
