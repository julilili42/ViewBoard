package com.example.viewboard.frontend.components.home.project

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import generateProjectCodeFromDbId
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.res.painterResource
import colorFromCode
import com.example.viewboard.R
import com.example.viewboard.frontend.components.utils.formatGermanShortDate
import com.example.viewboard.frontend.components.utils.formatRemaining

@Composable
fun ProjectCardTasks(
    issueName: String,
    projectId: String,
    dueDate: String,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val dateLabel = formatGermanShortDate(dueDate)
    val issueDueTime = formatRemaining(dueDate)
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Color.Black.copy(alpha = 0.2f)), shape = MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
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
                        val projectNameCode = generateProjectCodeFromDbId(projectId)
                        val projectColor = colorFromCode(projectNameCode)

                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(listOf(projectColor, projectColor.copy(alpha = 1f))),
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
                            Text(text = issueName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.calender_svgrepo_com),
                                    contentDescription = "Date",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = dateLabel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.hour_glass_svgrepo_com),
                                    contentDescription = "Due Date",
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = issueDueTime,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                        }
                    }
                    IconButton(onClick = onMenuClick) {
                        Icon(imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}



