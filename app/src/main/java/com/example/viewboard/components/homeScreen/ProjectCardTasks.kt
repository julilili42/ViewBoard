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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import generateProjectCode
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import colorFromCode

@Composable
fun ProjectCardTasks(
    name: String,
    dueDate: String,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    // 1) Parsen des Datums-Strings (nur der Datums-Teil)
    val datePart = dueDate
        .substringBefore('T')
        .substringBefore(' ')
        .trim()
    val dueLocalDate = LocalDate.parse(datePart)

    // 2) Label für Datum (relativ oder fix)
    val todayDate = LocalDate.now()
    val daysBetween = ChronoUnit.DAYS.between(todayDate, dueLocalDate).toInt()
    val dateLabel = when {
        daysBetween == 0 -> "Today"
        daysBetween == 1 -> "Tomorrow"
        daysBetween in 2..7 -> "$daysBetween days"
        else -> dueLocalDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .border(
                    BorderStroke(1.dp, Color.Black.copy(alpha = 0.2f)),
                    shape = MaterialTheme.shapes.medium
                )
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
                        val projectNameCode = generateProjectCode(name,dueDate)
                        val projectColor = colorFromCode(projectNameCode)

                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(projectColor, projectColor.copy(alpha = 1f))
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(0.dp)
                                .height(80.dp)
                                .fillMaxWidth(0.2f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = projectNameCode,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = dateLabel,
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

