package com.example.viewboard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.components.SectionCard
import com.example.viewboard.components.MenuItem
import com.example.viewboard.ui.navigation.BackButton

@Composable
fun HelpSupportScreen(modifier: Modifier = Modifier, navController: NavController) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            // Back arrow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Zentrierte Überschrift
                Text(
                    text = "Hilfe & Support",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )

                // BackButton rechts
                BackButton(
                    text = "Back",
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            Spacer(Modifier.size(24.dp))

            // FAQ
            SectionCard(title = "FAQ") {
                MenuItem("Wie verwende ich ViewBoard?", onClick = { /* TODO: Show FAQ */ })
                Divider()
                MenuItem("Passwort zurücksetzen", onClick = { /* TODO */ })
                Divider()
                MenuItem("Datenschutzrichtlinien", onClick = { /* TODO */ })
            }
            Spacer(Modifier.size(24.dp))

            SectionCard(title = "Kontakt") {
                MenuItem("E-Mail an Support", onClick = { /* TODO: Intent mailto: */ })
                Divider()
                MenuItem("Feedback senden", onClick = { /* TODO: Feedback-Form */ })
            }
            Spacer(Modifier.weight(1f))

            // app version
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
}