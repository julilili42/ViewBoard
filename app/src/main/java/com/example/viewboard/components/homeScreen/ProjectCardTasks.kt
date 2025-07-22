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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import generateProjectCodeFromDbId
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import colorFromCode
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.ui.issue.AvatarInitialBox
import com.example.viewboard.ui.issue.formatGermanShortDate
import com.example.viewboard.ui.issue.formatRemaining

@Composable
fun ProjectCardTasks(
    name: String,
    projectId: String,
    assignments: List<String>,
    dueDate: String,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    // Datum formatieren
    val dateLabel = formatGermanShortDate(dueDate)
    val issueDueTime = formatRemaining(dueDate)
    val avatarSize = 18.dp
    var expandedUser by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Lade E‑Mails asynchron
    val emailsState by produceState<List<String?>>(initialValue = emptyList(), key1 = assignments) {
        val result = runCatching { AuthAPI.getEmailsByIds(assignments) }
            .getOrNull()
            ?.getOrNull()
        value = result ?: emptyList()
    }

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
            // Hauptinhalt
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


                            Text(text = name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Avatare unten rechts einfügen
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) { expandedUser = true }
            ) {
                val showCount = 3
                val extraCount = (emailsState.size - showCount).coerceAtLeast(0)

                Row(
                    horizontalArrangement = Arrangement.spacedBy((-avatarSize / 3)),
                ) {
                    emailsState.take(showCount).forEach { email ->
                        email?.let { AvatarInitialBox(it, avatarSize) }
                    }
                    if (extraCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(avatarSize + 3.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$extraCount",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                // Dropdown mit allen Avataren
                androidx.compose.material3.DropdownMenu(
                    expanded = expandedUser,
                    onDismissRequest = { expandedUser = false },
                    modifier = Modifier
                        .wrapContentWidth()
                        .width(200.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        emailsState.forEach { email ->
                            email?.let { AvatarInitialBox(it, avatarSize) }
                        }
                    }
                }
            }
        }
    }
}



