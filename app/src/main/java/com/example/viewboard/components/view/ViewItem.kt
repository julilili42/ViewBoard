package com.example.viewboard.components.view

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
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import colorFromCode
import com.example.viewboard.backend.Timestamp
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueState
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.util.filterIssuesByStates
import generateProjectCode
import kotlinx.coroutines.flow.Flow

/**
 * @param name is the view name
 * @param creator is the creator name
 * @param count is the issue count from the view
 * @param color is the color of the ui element
 * @param onClick is the click event of the ui element
 */
@Composable
fun ViewItem(view: ViewLayout, creator: String, color: Color, onClick: () -> Unit) {
    val viewNameCode = generateProjectCode(view.name, view.creationTS)
    val viewNamecolor = colorFromCode(viewNameCode)

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
                    brush = Brush.linearGradient(listOf(viewNamecolor, viewNamecolor.copy(alpha = 0.6f))),
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
                            .background(viewNamecolor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = viewNameCode,
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
                Text(
                    text = Timestamp(data = view.creationTS).getDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = view.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(Modifier.weight(1f))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box{
                        Text(
                            text = creator,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = view.issues.size.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

