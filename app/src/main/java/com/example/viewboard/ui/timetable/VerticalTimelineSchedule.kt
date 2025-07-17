package com.example.viewboard.ui.timetable

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.viewboard.dataclass.Project
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import colorFromCode
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.components.homeScreen.IssueProgress
import com.example.viewboard.components.homeScreen.IssueProgressCalculator
import com.example.viewboard.components.homeScreen.TimeSpanFilter
import generateProjectCode
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import kotlin.random.Random

@Composable
fun VerticalTimelineSchedule(
    projects: List<ProjectLayout>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val totalPx          = constraints.maxHeight.toFloat()
        val monthPx          = totalPx / 365f
        val scrollState      = rememberScrollState()
        val density          = LocalDensity.current

        // Heute-Linie berechnen
        val today     = LocalDate.now()
        val monthFrac = (today.dayOfMonth - 1) / today.lengthOfMonth().toFloat()
        val todayPx   = ((today.monthValue - 1)*30 + monthFrac) * monthPx
        val todayDp   = with(density) { todayPx.toDp() }
        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                // 1) Feste Monats-Labels (nicht scrollbar)
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    listOf(
                        "Jan","Feb","Mar","Apr","May","Jun",
                        "Jul","Aug","Sep","Oct","Nov","Dec"
                    ).forEach { m ->
                        Box(
                            modifier = Modifier
                                .height(monthPx.toDp()*30)
                                .fillMaxWidth()
                                .drawBehind {
                                    val stroke = 2.dp.toPx()
                                    val x = size.width - stroke/2
                                    drawLine(
                                        color = primaryColor.copy(alpha = 0.5f),
                                        start = Offset(x, 0f),
                                        end = Offset(x, size.height),
                                        strokeWidth = stroke
                                    )
                                },

                            contentAlignment = Alignment.Center
                        ) {
                            Text(m, style = MaterialTheme.typography.bodySmall,color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                // 2) Scrollbarer Bereich (Phasen + gestrichelte Heute-Linie)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(scrollState)
                ) {
                    // gestrichelte Linie nur im scrollbaren Bereich
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val dash = 6.dp.toPx()
                        val extraOffset = 25.dp.toPx()
                        val yPos        = todayDp.toPx() + extraOffset

                        drawLine(
                            color       = primaryColor,//Color.Red,
                            start       = Offset(0f, yPos ),
                            end         = Offset(size.width, yPos),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect  = PathEffect.dashPathEffect(floatArrayOf(dash, dash), 0f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        projects.forEach { project ->
                            // Berechne hier pro Projekt direkt deine Offsets
                            val startDp  = (randomAccumulatedDays(project.startMonth - 1) * monthPx).toDp()
                            val heightDp = (randomAccumulatedDays(project.endMonth - project.startMonth + 1) * monthPx).toDp()

                            val projectNameCode = generateProjectCode(project.name)
                            val projectNamecolor = colorFromCode(projectNameCode)



                            Column(
                                modifier = Modifier
                                    .width(47.dp)
                                    .fillMaxHeight()
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Phase‑Label für dieses eine Projekt
                                ProjectLabel(
                                    name = projectNameCode,
                                    modifier = Modifier
                                        .offset(y = startDp)
                                        .widthIn(min = 45.dp)
                                        .background(
                                            brush = Brush.linearGradient(listOf(projectNamecolor, projectNamecolor.copy(alpha = 0.8f))),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )

                                Spacer(Modifier.height(8.dp))

                                // Balken **nur** für dieses eine Projekt
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
                                    VerticalMilestoneBar(
                                        project = project,
                                        total     = 4,
                                        colors    = primaryGradient,
                                        timeSpan = TimeSpanFilter.ALL_TIME,
                                        modifier  = Modifier
                                            .fillMaxSize()
                                            .padding(vertical = 1.dp),
                                        width     = 8.dp,
                                        spacing   = 1.dp,
                                        corner    = 4.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Scroll-Indikator
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

// VerticalMilestoneBar und ProjectLabel bleiben unverändert



// VerticalMilestoneBar bleibt unverändert
@Composable
fun VerticalMilestoneBar(
    project: ProjectLayout,
    timeSpan: TimeSpanFilter,
    total: Int=4,
    calculator: IssueProgressCalculator = remember { IssueProgressCalculator() },
    colors: List<Color>,
    modifier: Modifier = Modifier,
    width: Dp = 8.dp,
    spacing: Dp = 2.dp,
    corner: Dp = 4.dp
) {
    val brush = gradientColorList(colors.first(), colors.last(), total)
    val progress by produceState<IssueProgress>(
        initialValue = IssueProgress(0,0,0f),
        key1 = project.id,
        key2 = timeSpan
    ) {
        calculator
            .getProjectProgressFlow(project.id, timeSpan)
            .collect { value = it }
    }

    Column(
        modifier = modifier.width(width),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(total) { idx ->
            val fillFrac = (progress.completedIssues.toFloat() - idx).coerceIn(0f,1f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(corner))
                    .background(Color.LightGray.copy(alpha = 0.3f))
            ) {
                if (fillFrac > 0f) {
                    // Wähle die Basis-Farben für diesen Index
                    val baseColor = brush[idx]
                    val nextColor = brush[idx+1]

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fillFrac)
                            .clip(RoundedCornerShape(corner))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        baseColor.copy(alpha = 1f),    // oben etwas kräftiger
                                        nextColor.copy(alpha = 1f)     // unten etwas transparenter
                                    ),
                                    startY = 0.0f,
                                    endY   = Float.POSITIVE_INFINITY,
                                    tileMode = TileMode.Clamp
                                )
                            )
                    )
                }
            }
        }
    }
}



@Composable
fun ProjectLabel(
    name: String,
    textSize: TextUnit = 8.sp,
    modifier: Modifier = Modifier
) {
    // 1) Read your primary color here, in a Composable context:
    val arrowColor = Color.Black
    Box(
        modifier = modifier
            .drawBehind {
                // 2) Now use the captured arrowColor inside DrawScope
                val pointerWidth  = with(density) { 12.dp.toPx() }
                val pointerHeight = with(density) { 6.dp.toPx() }
                val cx = size.width / 2f
                val y0 = size.height

                val path = Path().apply {
                    moveTo(cx - pointerWidth/2, y0)
                    lineTo(cx + pointerWidth/2, y0)
                    lineTo(cx,                 y0 + pointerHeight)
                    close()
                }
                drawPath(path, color = arrowColor)
            }
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            fontSize = textSize,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground //MaterialTheme.colorScheme.primary
        )
    }
}

fun gradientColorList(
    startColor: Color,
    endColor:   Color,
    steps:      Int
): List<Color> {
    require(steps > 0) { "steps must be > 0" }
    return List(steps + 1) { i ->
        val fraction = i / steps.toFloat()      // 0f .. 1f
        lerp(startColor, endColor, fraction)
    }
}

fun randomAccumulatedDays(month: Int): Int {
    // Aktuelles Jahr
    val year = Year.now().value

    // Clamp month auf 1..12
    val m = month.coerceIn(1, 12)

    // 1) Summe der Tage von Monat 1 bis m
    val daysSum = (1..m).sumOf { mo ->
        YearMonth.of(year, mo).lengthOfMonth()
    }

    // 2) Zufallszugabe 0..31
    val randomExtra = Random.nextInt(from = 0, until = 32)

    return daysSum + randomExtra
}