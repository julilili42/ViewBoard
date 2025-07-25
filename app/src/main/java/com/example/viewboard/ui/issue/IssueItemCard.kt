package com.example.viewboard.ui.issue

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.viewboard.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import colorFromCode
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.NavScreens
import kotlinx.coroutines.launch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.sp
import colorFromEmail
import com.example.viewboard.ui.utils.emailToInitials
import com.example.viewboard.ui.utils.formatGermanShortDate
import com.example.viewboard.ui.utils.formatRemaining


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IssueItemCard(
    title: String,
    date: String,
    projectId: String,
    issueLabels: List<String>,
    emailsState:List<String?>,
    issueId: String,
    modifier: Modifier = Modifier,
    navController: NavController,
    onOptionsClick: () -> Unit = {}
) {
    val showCount = emailsState.size.coerceAtMost(3)
    val avatarSize = 18.dp
    val scope = rememberCoroutineScope()
    var expandedOptions by remember { mutableStateOf(false) }
    var expandedUser by remember { mutableStateOf(false) }
    val issueDate = formatGermanShortDate(date)
    val issueDueTime = formatRemaining(date)
    val scrollState = rememberScrollState()


    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
    ){
        Column(verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding( 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Column {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mehr Optionen",
                        modifier = Modifier
                            .combinedClickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    expandedOptions  = true
                                    onOptionsClick()
                                },
                                onLongClick = {}
                            )
                            .size(24.dp)
                    )

                    DropdownMenu(
                        expanded = expandedOptions ,
                        onDismissRequest = { expandedOptions  = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                expandedOptions  = false
                                navController.navigate(
                                    NavScreens.IssueEditNavScreens.createRoute(projectId = projectId.trim('{','}'), issueId =  issueId, )
                                )

                            }

                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                expandedOptions  = false

                                scope.launch {
                                    try {
                                        val cleanId = projectId.trim('{', '}')
                                        FirebaseAPI.rmIssue(projID = cleanId , id = issueId)
                                    } catch (e: Exception) {
                                        Log.e("IssueItemCard", "Error deleting issue", e)
                                    }
                                }
                            }
                        )
                    }
                }

            }

            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .defaultMinSize(minHeight = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (issueLabels.isEmpty()) {
                    Spacer(modifier = Modifier.height(0.dp))
                } else {
                    issueLabels.forEach { label ->
                        val labelColor = colorFromCode(label)
                        Box(
                            modifier = Modifier
                                .background(
                                    color = labelColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        text = issueDate,
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
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    val extraCount = (emailsState.size - showCount).coerceAtLeast(0)
                    Box(modifier.height(avatarSize).clickable(
                        interactionSource = remember { MutableInteractionSource()},
                        indication = null
                    )
                    {
                        expandedUser = true
                    }) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-avatarSize/3))
                            ,
                        ) {
                            emailsState.take(3).forEach { email ->
                                if (email != null) {
                                    AvatarInitialBox(email, avatarSize)
                                }
                            }
                            if (emailsState.size > 3) {
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
                        DropdownMenu(
                            expanded = expandedUser,
                            onDismissRequest = { expandedUser = false },
                            modifier = Modifier
                                .wrapContentWidth()
                                .width(200.dp)
                                .background(Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .padding(top = 4.dp, bottom = 4.dp, end = 8.dp, start = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Log.d("EmailsList", "No emails found for IDs: $emailsState")
                                emailsState.forEach { email ->
                                    if (email != null) {
                                        AvatarInitialBox(email, avatarSize)
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}



@Composable
fun AvatarInitialBox(email: String, avatarSize: Dp) {
    val initials = emailToInitials(email)
    val initialsColor = colorFromEmail(email)
    Box(
        modifier = Modifier
            .size(avatarSize + 3.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier
            .size(avatarSize)
            .clip(CircleShape)
            .background(initialsColor)
            .wrapContentSize(Alignment.Center)) {
            Text(
                text = initials,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, fontSize = 8.sp),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

