package com.example.viewboard.components.HomeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.viewboard.R

@Composable
fun ProfileHeader(
    name: String,
    subtitle: String,
    onProfileClick: () -> Unit
) {
    //
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        // Profilbild ganz links, klickbar
        Image(
            painter = painterResource(id = R.drawable.pb_raoul),
            contentDescription = "Profil",
            modifier = Modifier
                .size(48.dp)
                // Border mit Primary-Farbe und Kreisform
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                // Bild selbst auf Kreis clippen
                .clip(CircleShape)
                .clickable(onClick = onProfileClick)
        )
        Spacer(modifier = Modifier.width(12.dp))

        // Text-Block mit Name + Untertitel
        Column {
            Text(
                text = "Hi $name",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}
