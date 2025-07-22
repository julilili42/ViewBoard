package com.example.viewboard.ui.issue

import android.net.Uri
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
    var expandedOptions by remember { mutableStateOf(false) }
    var expandedUser by remember { mutableStateOf(false) }
    val issueDate = formatGermanShortDate(date)
    val issueDueTime = formatRemaining(date)
    val scrollState = rememberScrollState()

   /* Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,

                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    )*/   Card(
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


            val exampleLabels = listOf(
                "Bug",
                "Feature",
                "High Priority",
                "Low Priority",
                "In Review",
                "Blocked",
                "UI",
                "Backend",
                "Research",
                "Documentation"
            )
            val mails = listOf(
                "Raoul.dankert@gmail.com", // John Doe
                "felix.dankert@gmail.com", // Alice Liddell
                "paul.dankert@gmail.com", // Bob Smith
                "Jerrry.dankert@gmail.com", // Eve Villanueva
                "DSad.dankert@gmail.com", // Karl Müller
                "Twdda.dankert@gmail.com", // Ute Tannhäuser
                "wadwam.dankert@gmail.com", // Maria García
                "wadwa.dankert@gmail.com", // Yvonne Thäter
                "wadwad.dankert@gmail.com", // Xaver Zimmermann
                "fwad.dankert@gmail.com"   // Quentin (einzelnes Zeichen)
            )

            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                exampleLabels.forEach { label ->
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
                    val avatarInitials = listOf(
                        "JD", // John Doe
                        "AL", // Alice Liddell
                        "BS", // Bob Smith
                        "EV", // Eve Villanueva
                        "KM", // Karl Müller
                        "UT", // Ute Tannhäuser
                        "MG", // Maria García
                        "YT", // Yvonne Thäter
                        "XZ", // Xaver Zimmermann
                        "Q"   // Quentin (einzelnes Zeichen)
                    )
                    val avatarEmails = listOf(
                        "john.doe@example.com",        // JD
                        "alice.liddell@example.com",   // AL
                        "bob.smith@example.com",       // BS
                        "eve.villanueva@example.com",  // EV
                        "karl.mueller@example.com",    // KM
                        "ute.tannhaeuser@example.com", // UT
                        "maria.garcia@example.com",    // MG
                        "yvonne.thaeter@example.com",  // YT
                        "xaver.zimmermann@example.com",// XZ
                        "quentin@example.com"          // Q
                    )

                    val extraCount = (avatarEmails.size - showCount).coerceAtLeast(0)
                    Box(modifier.clickable(
                        interactionSource = remember { MutableInteractionSource()},
                        indication = null
                    ) {
                        expandedUser = true
                    }) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-avatarSize/3))
                            , modifier = modifier.background(Color.White)
                        ) {
                            avatarEmails.take(3).forEach { email ->
                                AvatarInitialBox(email, avatarSize)
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
                                avatarEmails.forEach { email ->
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



@Composable
private fun AvatarInitialBox(email: String, avatarSize: Dp) {
    val initials = emailToInitials(email)
    val initialsColor = colorFromCode(email)
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
    // 1) Nur das Datum+Zeit‑Teil vor 'T' oder Leerzeichen
    val tsPart = isoTimestamp.substringBefore('Z') // entfernt Z, falls vorhanden
    val instant = Instant.parse(
        tsPart.substringBefore(' ') // falls ein Leerzeichen im String ist
            .let { if (it.contains('T')) it else it + "T00:00:00" } + "Z"
    )

    val now = Instant.now()
    if (instant.isBefore(now)) {
        return "0 hours"
    }

    // 2) Wandeln in LocalDate/LocalDateTime für Datumsermittlung
    val zone  = ZoneId.systemDefault()
    val thenDt = instant.atZone(zone).toLocalDateTime()
    val nowDt  = now.atZone(zone).toLocalDateTime()

    return if (thenDt.toLocalDate() == nowDt.toLocalDate()) {
        // gleiche Tages‑Datum → Stunden
        val hours = ChronoUnit.HOURS.between(nowDt, thenDt).toInt()
        val h     = hours.coerceAtLeast(0)
        if (h == 1) "1 hour" else "$h hours"
    } else {
        // anderes Datum → Tage
        val days = ChronoUnit.DAYS.between(nowDt.toLocalDate(), thenDt.toLocalDate()).toInt()
        val d    = days.coerceAtLeast(0)
        if (d == 1) "1 day" else "$d days"
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

