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
import java.time.LocalDate

@Composable
fun VerticalTimelineSchedule(
    projects: List<Project>,
    phases:   List<String>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val totalPx          = constraints.maxHeight.toFloat()
        val monthPx          = totalPx / 12f
        val scrollState      = rememberScrollState()
        val density          = LocalDensity.current

        // Heute-Linie berechnen
        val today     = LocalDate.now()
        val monthFrac = (today.dayOfMonth - 1) / today.lengthOfMonth().toFloat()
        val todayPx   = ((today.monthValue - 1) + monthFrac) * monthPx
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
                                .height(monthPx.toDp())
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
                            Text(m, style = MaterialTheme.typography.bodySmall,color = MaterialTheme.colorScheme.surface)
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
                        phases.forEach { phase ->
                            val phaseProjects = projects.filter { it.phase == phase }
                            Column(
                                modifier = Modifier
                                    .width(45.dp)
                                    .fillMaxHeight()
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Phase-Label
                                if (phaseProjects.isNotEmpty()) {
                                    val firstStartDp = ((phaseProjects.minOf { it.startMonth } - 1) * monthPx).toDp()
                                    ProjectLabel(
                                        name = phase,
                                        modifier = Modifier
                                            .offset(y = firstStartDp)
                                            .widthIn(min = 40.dp)
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                // Projekt-Balken
                                phaseProjects.forEach { p ->
                                    val startDp  = ((p.startMonth - 1) * monthPx).toDp()
                                    val heightDp = ((p.endMonth - p.startMonth + 1) * monthPx).toDp()

                                    Box(
                                        modifier = Modifier
                                            .offset(y = startDp)
                                            .height(heightDp)
                                            .width(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(p.color.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        val primaryGradient = listOf(

                                            MaterialTheme.colorScheme.secondary.copy(alpha = 1.0f),

                                            MaterialTheme.colorScheme.primary.copy(alpha = 1.0f),


                                        )
                                        VerticalMilestoneBar(
                                            total     = p.totalMilestones,
                                            completed = p.completedMilestones,
                                            colors    =  primaryGradient,
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
    total: Int,
    completed: Float,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    width: Dp = 8.dp,
    spacing: Dp = 2.dp,
    corner: Dp = 4.dp
) {
    val brush = gradientColorList(colors.first(), colors.last(), total)
    Column(
        modifier = modifier.width(width),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(total) { idx ->
            val fillFrac = ((completed - idx).coerceIn(0f,1f))
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
    val arrowColor = MaterialTheme.colorScheme.surface
    Box(
        modifier = modifier
            .background(
                color = arrowColor,
                shape = RoundedCornerShape(4.dp)
            )
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
            .padding(horizontal = 8.dp, vertical = 4.dp),
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


@Composable
fun LabeledBar(
    label: @Composable () -> Unit,
    bar:   @Composable () -> Unit
) {
    Layout(
        content = {
            // Reihenfolge wichtig: Erst das Label, dann die Bar
            Box(Modifier) { label() }
            Box(Modifier) { bar() }
        }
    ) { measurables, constraints ->
        // 1) Miss Label (unbegrenzt breit, unbegrenzt hoch)
        val labelPlaceable = measurables[0].measure(constraints)

        // 2) Miss die Bar, beschränke Höhe auf verbleibenden Platz
        //    (hier: Bar darf maximal constraints.maxHeight - labelHeight hoch sein)
        val barConstraints = constraints.copy(
            maxWidth  = constraints.maxWidth,
            maxHeight = (constraints.maxHeight - labelPlaceable.height).coerceAtLeast(0)
        )
        val barPlaceable = measurables[1].measure(barConstraints)

        // 3) Gesamtgröße: Breite = max(Label, Bar), Höhe = Label + Bar
        val width  = maxOf(labelPlaceable.width, barPlaceable.width)
        val height = labelPlaceable.height + barPlaceable.height

        layout(width, height) {
            // Label oben zentriert
            val labelX = (width - labelPlaceable.width) / 2
            labelPlaceable.placeRelative(x = labelX, y = 0)

            // Bar direkt darunter, ebenfalls zentriert
            val barX = (width - barPlaceable.width) / 2
            barPlaceable.placeRelative(x = barX, y = labelPlaceable.height)
        }
    }
}
fun gradientBrushBetween(
    startColor: Color,
    endColor:   Color,
    steps:      Int
): Brush {
    // 1) Erstelle die List von (Position, Color) Stops
    val stops = List(steps + 1) { i ->
        val fraction = i / steps.toFloat()     // 0f .. 1f
        fraction to lerp(startColor, endColor, fraction)
    }.toTypedArray()

    // 2) Rückgabe des Brushes mit den Stops
    return Brush.verticalGradient(colorStops = stops)
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