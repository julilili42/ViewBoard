package com.example.viewboard.ui.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MilestoneBar2(
    total: Int,
    completed: Float,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    spacing: Dp = 2.dp,
    corner: Dp = 4.dp
) {
    // 1) Erzeuge den horizontalen Gradient über alle Segmente
    val gradientBrush = Brush.horizontalGradient(colors)

    Row(
        modifier = modifier
            .height(height)
            // Hintergrund-Pinsel statt einzelner Farben
            .clip(RoundedCornerShape(corner))
            .background(gradientBrush),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(total) { idx ->
            val fillFraction = (completed - idx).coerceIn(0f, 1f)

            // 2) Jeder Segment-Box wird nur noch die Höhe nach fillFraction beschnitten,
            //    die Farbe kommt vom dahinterliegenden Gradient
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(fillFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(corner))
                    // abgeschlossene Teile transparent, zeigen den Gradient
                    .background(Color.Transparent)
            ) {
                // Nichts weiter nötig, der Gradient ist ja die eigentliche Füllung
            }

            // 3) Wenn nicht abgeschlossen, lege eine halbtransparente Graustufe drüber
            if (fillFraction < 1f) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(corner))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
            }
        }
    }
}
