package com.example.viewboard.components.Timetable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SegmentedSwitch(
    modifier: Modifier = Modifier,
    options: Pair<String, String> = "Projects" to "Issues",
    selectedLeft: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .height(40.dp)
            .width(150.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        // 1) Berechne Breite pro Segment
        val segmentWidth = maxWidth / 2f

        // 2) animierter Offset: 0 für Links, segmentWidth für Rechts
        val offsetX by animateDpAsState(
            targetValue = if (selectedLeft) 0.dp else segmentWidth,
            animationSpec = tween(durationMillis = 300)
        )

        // 3) der bewegte Indikator
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .width(segmentWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary)
        )

        // 4) die beiden Klick-Flächen
        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSelectionChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = options.first,
                    fontSize = 10.sp,
                    color = if (selectedLeft) Color.White else MaterialTheme.colorScheme.onBackground
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onSelectionChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = options.second,
                    fontSize = 10.sp,
                    color = if (!selectedLeft) Color.White else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
