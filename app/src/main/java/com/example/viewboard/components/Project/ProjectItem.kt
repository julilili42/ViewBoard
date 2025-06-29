package com.example.viewboard.components.HomeScreen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import coil.compose.AsyncImage
/**
 * Ein Project-Item mit Gradient-Hintergrund, Pill-Phase, Titel, Zeitraum,
 * Fortschrittsbalken und unten links überlappenden Avataren.
 *
 * @param name                Projektname
 * @param phase               Kürzel (z.B. "#A23")
 * @param startMonth          Startmonat (1–12)
 * @param endMonth            Endmonat (1–12)
 * @param color               Basisfarbe für den Hintergrund‐Gradient
 * @param totalMilestones     Gesamtzahl der Meilensteine
 * @param completedMilestones Bereits abgeschlossene Meilensteine
 * @param avatarUris          Liste von URIs zu lokalen Profilbildern
 * @param onClick             Callback bei Klick auf die gesamte Card
 */
@Composable
fun ProjectItem(
    name: String,
    phase: String,
    startMonth: Int,
    endMonth: Int,
    color: Color,
    totalMilestones: Int,
    completedMilestones: Float,
    avatarUris: List<Uri>,
    onClick: () -> Unit
) {
    val progress = (completedMilestones / totalMilestones).coerceIn(0f, 1f)
    val startLabel = Month.of(startMonth)
        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val endLabel = Month.of(endMonth)
        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val showCount = avatarUris.size.coerceAtMost(3)
    val avatarSize = 18.dp
    val avatarOverlap = 12.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            Modifier
                .background(
                    brush = Brush.linearGradient(listOf(color, color.copy(alpha = 0.6f))),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .background(color, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = phase,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Details",
                        tint = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "$startLabel – $endLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.weight(1f))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(avatarSize),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar-Stack (überlappend)
                    Box {
                        avatarUris.take(showCount).forEachIndexed { index, uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(avatarSize)
                                    .offset(x = index * avatarOverlap)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.White, CircleShape)
                            )
                        }
                        val extra = avatarUris.size - showCount
                        if (extra > 0) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(avatarSize)
                                    .offset(x = showCount * avatarOverlap)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.dp, Color.White, CircleShape)
                            ) {
                                Text(
                                    text = "+$extra",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .width(80.dp)
                            .height(8.dp),
                        trackColor = Color.White.copy(alpha = 0.3f),
                        color = Color.White
                    )
                }
            }
        }
    }
}

