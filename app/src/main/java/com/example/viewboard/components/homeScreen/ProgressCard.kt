package com.example.viewboard.components.homeScreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.viewboard.backend.dataLayout.IssueDeadlineFilter
import kotlin.math.roundToInt

private enum class Period(val label: String, val short: String) {
    WEEKLY("Weekly", "W"),
    MONTHLY("Monthly", "M"),
    YEARLY("Yearly", "Y");

    fun next() = when (this) {
        WEEKLY -> MONTHLY
        MONTHLY -> YEARLY
        YEARLY -> WEEKLY
    }
}

@Composable
fun ProgressCard(
    progress: Float,
    modifier: Modifier = Modifier,
    title: IssueDeadlineFilter,
    onClick: () -> Unit = {}
) {
    var period by remember { mutableStateOf(title) }
    val title = "${period.label} Targets"
    val percent = progress.roundToInt()
    val targetFraction = (progress.coerceIn(0f, 100f)) / 100f
    val animatedProgress by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    )
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {onClick()
                            period = period.next()
                             },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = period.short,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You completed $percent% of your ${period.label} Targets.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
