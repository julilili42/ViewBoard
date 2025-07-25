package com.example.viewboard.frontend.components.timetable

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
fun VerticalMilestoneBar(
    project: ProjectLayout,
    width: Dp = 8.dp,
    spacing: Dp = 2.dp,
    corner: Dp = 4.dp,
    timeSpan: IssueDeadlineFilter,
    total: Int=4,
    colors: List<Color>,
    calculator: IssueProgressCalculator = remember { IssueProgressCalculator() },
    modifier: Modifier = Modifier,
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
                    // Color for index
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
                                        baseColor.copy(alpha = 1f),
                                        nextColor.copy(alpha = 1f)
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