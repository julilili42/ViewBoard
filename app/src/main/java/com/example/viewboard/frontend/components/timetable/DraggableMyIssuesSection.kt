package com.example.viewboard.frontend.components.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.frontend.screens.issue.MyTasksScreen
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DraggableMyIssuesSection(
    navController: NavController,
    onSortClick: () -> Unit,
    issues: List<IssueLayout>,
    selectedDate: LocalDate? = null,
    modifier: Modifier = Modifier,
    minSheetHeightPx: Float = 0f
) {
    val density = LocalDensity.current
    var currentSheetHeightPx by remember { mutableStateOf(0f) }
    val matchingIssues = remember(selectedDate, issues) {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        issues.filter { issue ->
            runCatching {
                val odt = OffsetDateTime.parse(issue.deadlineTS, formatter)
                odt.toLocalDate()
            }.getOrNull() == selectedDate
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val maxHeightPx = with(density) { maxHeight.toPx() }

        if (currentSheetHeightPx == 0f && maxHeightPx > 0f) {
            currentSheetHeightPx = minSheetHeightPx.coerceAtMost(maxHeightPx)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { currentSheetHeightPx.toDp() })
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .align(Alignment.BottomCenter)
                .background(Color.White)
        ) {
            // Drag and Drop
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .pointerInput(Unit) {}
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            val newHeight = currentSheetHeightPx - delta
                            currentSheetHeightPx = newHeight.coerceIn(minSheetHeightPx, maxHeightPx * 0.8f) // Max 80%
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), shape = MaterialTheme.shapes.small)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                }
            }
            MyTasksScreen(
                navController = navController,
                issues = matchingIssues,
                onSortClick = onSortClick,
            )
        }
    }
}