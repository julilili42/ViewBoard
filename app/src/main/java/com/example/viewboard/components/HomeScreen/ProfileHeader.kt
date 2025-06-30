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
import com.example.viewboard.ui.navigation.BackButton
import com.example.viewboard.ui.navigation.hasSoftNavigationBar

@Composable
fun ProfileHeader(
    name: String,
    subtitle: String,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val usesSoftNav = hasSoftNavigationBar()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profilbild
        Image(
            painter = painterResource(id = R.drawable.pb_raoul),
            contentDescription = "Profil",
            modifier = Modifier
                .size(48.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clip(CircleShape)
                .clickable(onClick = onProfileClick)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Name + Untertitel
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

        Spacer(modifier = Modifier.weight(1f))

        // Zeige BackButton ganz rechts nur, wenn Soft-Navigation aktiv ist
        if (true) {
            BackButton(
                text = "Back",
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = onBackClick
            )
        }
    }
}
