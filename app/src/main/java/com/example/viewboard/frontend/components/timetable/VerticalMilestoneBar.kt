package com.example.viewboard.frontend.components.timetable

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.viewboard.backend.dataLayout.IssueDeadlineFilter
import com.example.viewboard.backend.dataLayout.IssueProgress
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.frontend.components.home.issueProgress.IssueProgressCalculator
import com.example.viewboard.frontend.components.utils.gradientColorList
@Composable
fun VerticalProgressBar(
    project: ProjectLayout,
    width: Dp = 8.dp,
    corner: Dp = 4.dp,
    timeSpan: IssueDeadlineFilter,
    colors: List<Color>,
    calculator: IssueProgressCalculator = remember { IssueProgressCalculator() },
    modifier: Modifier = Modifier,
) {
    val progress by produceState<IssueProgress>(
        initialValue = IssueProgress(0, 0, 0f),
        key1 = project.id,
        key2 = timeSpan
    ) {
        calculator
            .getProjectProgressFlow(project.id, timeSpan)
            .collect { value = it }
    }

    val progressFraction = (progress.percentComplete / 100f).coerceIn(0f, 1f)
    Log.d("progress", "progress: $progressFraction")
    Box(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .clip(RoundedCornerShape(corner))
            .background(Color.LightGray.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(progressFraction)
                .clip(RoundedCornerShape(corner))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colors.first(), colors.last()),
                        tileMode = TileMode.Clamp
                    )
                )
        )
    }
}