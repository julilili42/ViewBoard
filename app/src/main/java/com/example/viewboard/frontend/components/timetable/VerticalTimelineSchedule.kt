package com.example.viewboard.frontend.components.timetable

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavController
import colorFromCode
import com.example.viewboard.backend.dataLayout.IssueDeadlineFilter
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.frontend.components.utils.dayOfYearFromIso
import com.example.viewboard.frontend.components.utils.toDp
import generateProjectCodeFromDbId
import java.time.LocalDate

@Composable
fun VerticalTimelineSchedule(
    projects: List<ProjectLayout>,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val totalPx = constraints.maxHeight.toFloat()
        val monthPx = totalPx / 365f
        val scrollState = rememberScrollState()
        val density = LocalDensity.current
        val todayInDays = dayOfYearFromIso(LocalDate.now().toString())
        val todayInDaysPx = (todayInDays * monthPx) + 60
        val todayInDaysDP = with(density) { todayInDaysPx.toDp() }
        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    listOf(
                        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                    ).forEach { m ->
                        Box(
                            modifier = Modifier
                                .height(monthPx.toDp() * 30)
                                .fillMaxWidth()
                                .drawBehind {
                                    val stroke = 2.dp.toPx()
                                    val x = size.width - stroke / 2
                                    drawLine(
                                        color = primaryColor.copy(alpha = 0.5f),
                                        start = Offset(x, 0f),
                                        end = Offset(x, size.height),
                                        strokeWidth = stroke
                                    )
                                },

                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                m,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // scrollable area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(scrollState)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val dash = 6.dp.toPx()

                        val yPos = todayInDaysDP.toPx()

                        drawLine(
                            color = primaryColor,
                            start = Offset(0f, yPos),
                            end = Offset(size.width, yPos),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash), 0f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        projects.forEach { project ->
                            val startDateToDays = dayOfYearFromIso(project.startTS)
                            val endDateToDays = dayOfYearFromIso(project.deadlineTS)
                            val dayDiff = endDateToDays - startDateToDays
                            val startDp = (startDateToDays * monthPx + 60).toDp()
                            val heightDp = (dayDiff * monthPx - 30).toDp()
                            val projectNameCode = generateProjectCodeFromDbId(project.id)
                            val projectNamecolor = colorFromCode(projectNameCode)
                            val total = (dayDiff / 15).toInt().coerceAtLeast(1)
                            Log.d("total", "total: $total")
                            Column(
                                modifier = Modifier
                                    .width(47.dp)
                                    .fillMaxHeight()
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                ProjectLabel(
                                    name = projectNameCode,
                                    modifier = Modifier
                                        .offset(y = startDp)
                                        .widthIn(min = 45.dp)
                                        .background(
                                            brush = Brush.linearGradient(
                                                listOf(
                                                    projectNamecolor,
                                                    projectNamecolor.copy(alpha = 0.8f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )

                                Spacer(Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .offset(y = startDp)
                                        .height(heightDp)
                                        .width(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(projectNamecolor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    val primaryGradient = listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.primary
                                    )
                                    VerticalProgressBar(
                                        project = project,
                                        colors = primaryGradient,
                                        timeSpan = IssueDeadlineFilter.ALL_TIME,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(vertical = 1.dp),
                                        width = 8.dp,
                                        corner = 4.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            val progress = if (scrollState.maxValue > 0)
                scrollState.value.toFloat() / scrollState.maxValue else 0f

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}




