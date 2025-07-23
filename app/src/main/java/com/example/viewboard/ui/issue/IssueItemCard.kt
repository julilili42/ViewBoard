package com.example.viewboard.ui.issue

import android.net.Uri
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
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import com.example.viewboard.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import colorFromCode
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.BottomBarScreen
import com.example.viewboard.ui.navigation.Screen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.sp
import colorFromEmail
import com.example.viewboard.backend.auth.impl.AuthAPI
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IssueItemCard(
    title: String,
    date: String,
    attachments: Int,
    projectId: String,
    assignments: List<String>,
    issuelabels: List<String>,
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
    var expandedOptions by remember { mutableStateOf(false) }
    var expandedUser by remember { mutableStateOf(false) }
    val issueDate = formatGermanShortDate(date)
    val issueDueTime = formatRemaining(date)
    val scrollState = rememberScrollState()
    val emailsState by produceState<List<String?>>(
        initialValue = emptyList(),
        key1 = assignments
    ) {
        // Lade die E‑Mails; bei Fehler oder leerem Ergebnis bleibt es bei emptyList
        val result = runCatching { AuthAPI.getEmailsByIds(assignments) }
            .getOrNull()
            ?.getOrNull()
        value = result ?: emptyList()
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
        //.padding( 16.dp)

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
                            // z.B. in deinem DropdownMenuItem “Edit”
                            onClick = {
                                expandedOptions  = false
                                val cleanProj = projectId.trim('{','}')

                                navController.navigate(
                                    Screen.IssueEditScreen.createRoute(projectId.trim('{','}'),"", issueId)
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

                                    }
                                }
                            }
                        )
                    }
                }

            }





            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                issuelabels.forEach { label ->
                    val labelColor= colorFromCode(label)
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
                    Box(modifier.clickable(
                        interactionSource = remember { MutableInteractionSource()},
                        indication = null
                    ) {
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
                            if (extraCount > 3) {
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
                            // Horizontal scrollbare Leiste mit allen Avataren
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
fun formatGermanShortDate(input: String): String {
    // 1) Nur den Datums‑Teil nehmen (vor 'T' oder Leerzeichen)
    val datePart = input
        .substringBefore('T')
        .substringBefore(' ')
        .trim()

    // 2) In LocalDate parsen
    val date = LocalDate.parse(datePart)  // wirft bei ungültigem Format

    // 3) Formatter für "DD. MMM yy" in Deutsch
    val formatter = DateTimeFormatter.ofPattern("dd. MMM yy", Locale.GERMAN)

    // 4) Formatiertes Ergebnis zurückliefern
    return date.format(formatter)
}

fun formatRemaining(isoTimestamp: String): String {
    // 1) Parse das Instant in UTC und addiere 2 Stunden
    val instantUtc = Instant.parse(isoTimestamp).plus(1, ChronoUnit.HOURS)

    val now = Instant.now()
    if (instantUtc.isBefore(now)) {
        return "expired"
    }

    // 2) Berechne die Dauer zwischen jetzt und dem korrigierten Zeitpunkt
    val duration = Duration.between(now, instantUtc)

    return if (duration.toHours() < 24) {
        val hours = duration.toHours().toInt().coerceAtLeast(0)
        when (hours) {
            0    -> "expired"
            1    -> "1 hour"
            else -> "$hours hours"
        }
    } else {
        val days = duration.toDays().toInt()
        when (days) {
            0    -> "expired"
            1    -> "1 day"
            else -> "$days days"
        }
    }
}

fun emailToInitials(email: String): String {
    // 1) Lokalen Teil vor '@' extrahieren
    val local = email.substringBefore('@', "").lowercase()

    // 2) Auftrenner definieren und splitten (explizites ignoreCase nötig)
    val separators = arrayOf(".", "_", "-")
    val parts = local.split(
        *separators,
        ignoreCase = true   // jetzt passt die Signatur vararg String + Boolean
    ).filter { it.isNotBlank() }

    // 3) Initialen bestimmen wie gehabt
    val initials = when {
        parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}"
        local.length >= 2 -> "${local[0]}${local[1]}"
        local.length == 1 -> "${local[0]}"
        else -> "??"
    }

    return initials.uppercase()
}

