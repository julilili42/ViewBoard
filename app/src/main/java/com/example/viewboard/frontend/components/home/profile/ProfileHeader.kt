package com.example.viewboard.frontend.components.home.profile

import androidx.compose.foundation.clickable
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
import com.example.viewboard.frontend.navigation.utils.BackButton

@Composable
fun ProfileHeader(
    name: String,
    subtitle: String,
    showBackButton: Boolean,
    navController: NavController,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfilePicture(
            painter = painterResource(id = R.drawable.logotest),
            contentDescription = "Profilbild Raoul",
            size = 48.dp,
            borderColor = MaterialTheme.colorScheme.primary,
            borderWidth = 3.dp,
            modifier = Modifier.clickable { onProfileClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.clickable { onProfileClick() }
        ) {
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

        if (showBackButton) {
            BackButton(
                text = "Back",
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = onBackClick
            )
        }
    }
}
