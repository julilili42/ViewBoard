package com.example.viewboard.ui.issue

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.example.viewboard.R
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.navigation.Screen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IssueItemCard(
    title: String,
    date: String,
    attachments: Int,
    projectId: String,
    issueId: String,
    state: String="",
    modifier: Modifier = Modifier,
    navController: NavController,
    avatarUris: List<Uri>,
    onOptionsClick: () -> Unit = {}
) {
    val showCount = avatarUris.size.coerceAtMost(3)
    val avatarSize = 18.dp
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    val instant = remember(date) { Instant.parse(date) }
    val localDt = remember(instant) {
        instant.atZone(ZoneId.systemDefault())
    }
    val fmt = remember {
        DateTimeFormatter.ofPattern("yyyy‑MM‑dd HH:mm")
    }
    val display = remember(localDt) {
        localDt.format(fmt)
    }

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Color.Black.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,

                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    expanded = true
                                    onOptionsClick()
                                },
                                onLongClick = {}
                            )
                            .size(24.dp)
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            // z.B. in deinem DropdownMenuItem “Edit”
                            onClick = {
                                expanded = false
                                val cleanProj = projectId.trim('{','}')

                                navController.navigate(
                                    Screen.IssueEditScreen.createRoute(projectId.trim('{','}'), issueId)
                                )

                            }

                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                expanded = false

                                scope.launch {
                                    try {
                                        val cleanId = projectId.trim('{', '}')
                                        FirebaseAPI.rmIssue(projID = cleanId , id = issueId)
                                    } catch (e: Exception) {

                                    }
                                }
                            }
                        )
                    }
                }

            }



            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                   /* Text(
                        text = priority,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )*/
                }
                Box(
                    Modifier
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                   /* Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )*/
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
                        text = display,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.hour_glass_svgrepo_com),
                        contentDescription = "Due Date",
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = attachments.toString() + " days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                   /* Icon(
                        painter = painterResource(id = R.drawable.create_note_svgrepo_com__1_),
                        contentDescription = "Creator",
                        modifier = Modifier.size(17.dp)
                    )
                    Box (  Modifier
                        .size(avatarSize +3.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.surface,
                            CircleShape
                        ),
                        contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = firstAvatar,
                            contentDescription = null,
                            modifier = Modifier
                                .size(avatarSize)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.surface,
                                    CircleShape
                                ),
                        )
                    }*/
                }
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {

                        avatarUris.take(showCount).forEachIndexed { index, uri ->
                            Box (  Modifier
                                .size(avatarSize +3.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.surface,
                                    CircleShape
                                ),
                                contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(avatarSize)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.surface,
                                            CircleShape
                                        ),
                                )
                            }
                        }
                        val extra = avatarUris.size - showCount
                        if (extra > 0) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(avatarSize +2.dp)
                                    //.offset(x = (showCount * avatarOverlap)) // nach den sichtbaren Avataren
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.surface,
                                        CircleShape
                                    )
                            ) {
                                Text(
                                    text = "+$extra",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )

                        }
                    }
                }
            }
        }
    }
}
