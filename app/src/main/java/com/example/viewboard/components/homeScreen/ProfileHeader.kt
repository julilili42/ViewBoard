package com.example.viewboard.components.homeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.components.ProfilePicture
import com.example.viewboard.ui.navigation.BackButton
import com.example.viewboard.ui.navigation.hasSoftNavigationBar

@Composable
fun ProfileHeader(
    name: String,
    subtitle: String,
    showBackButton: Boolean ,
    navController: NavController,
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

        ProfilePicture(
            painter = painterResource(id = R.drawable.pb_raoul),
            contentDescription = "Profilbild Raoul",
            size = 48.dp,
            borderColor = MaterialTheme.colorScheme.primary,
            borderWidth = 3.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Name + Untertitel
        Column {
            Text(
                text = AuthAPI.getCurrentDisplayName() ?: "failed to load username",
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
        if (showBackButton) {
            BackButton(
                text = "Back",
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = onBackClick
            )
        }
    }
}
