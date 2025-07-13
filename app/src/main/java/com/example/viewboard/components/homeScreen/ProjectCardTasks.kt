package com.example.viewboard.components.homeScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color

/**
 * A card representing a single task with dynamic timestamp (date label + time),
 * gradient background, and a menu placeholder. Swipe-to-dismiss logic remains external.
 *
 * @param name The task title
 * @param dueDateTime The due date and time of the task
 * @param onClick Called when the card is clicked
 * @param onMenuClick Called when the menu icon is clicked
 */
@Composable
fun ProjectCardTasks(
    name: String,
    dueDateTime: LocalDateTime,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    // Date relative label
    val todayDate = LocalDate.now()
    val dueDate = dueDateTime.toLocalDate()
    val daysBetween = ChronoUnit.DAYS.between(todayDate, dueDate).toInt()
    val dateLabel = when {
        daysBetween <= 1 -> "Today"
        daysBetween in 2..7 -> "${daysBetween} days"
        else -> dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }
    // Time label
    val timeLabel = dueDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))

    Card(
        shape = MaterialTheme.shapes.medium,

        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = onClick)
    ) {
        // Gradient background for the card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                // 1) dünner grauer Rand
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
                    shape = MaterialTheme.shapes.medium,

                )
                // 2) weißer Hintergrund
                .background(
                    color = Color.White,
                    shape = MaterialTheme.shapes.medium
                )

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val initials = "#A15"

                            Box(
                                modifier = Modifier
                                    .padding(0.dp)
                                    .height(80.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.TopStart
                            ) {
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.Center)  // <— Position im Box-Koordinatensystem
                                        .padding(10.dp)
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            ) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$dateLabel • $timeLabel",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF000113)

                            )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF000113)
                        )
                            }
                    }
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
