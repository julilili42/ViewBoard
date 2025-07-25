package com.example.viewboard.frontend.components.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProjectLabel(
    name: String,
    textSize: TextUnit = 8.sp,
    modifier: Modifier = Modifier
) {
    val arrowColor = Color.Black
    Box(
        modifier = modifier
            .drawBehind {
                val pointerWidth = with(density) { 12.dp.toPx() }
                val pointerHeight = with(density) { 6.dp.toPx() }
                val cx = size.width / 2f
                val y0 = size.height

                val path = Path().apply {
                    moveTo(cx - pointerWidth / 2, y0)
                    lineTo(cx + pointerWidth / 2, y0)
                    lineTo(cx, y0 + pointerHeight)
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
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}