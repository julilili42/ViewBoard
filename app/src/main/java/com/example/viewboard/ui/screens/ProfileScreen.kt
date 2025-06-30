package com.example.viewboard.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.ui.navigation.Screen

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, navController: NavController) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        var showNotifDialog by remember { mutableStateOf(false) }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 24.dp)
        ) {
            item {
                // profile image
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.pb_raoul),
                            contentDescription = "Profilbild",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = "Raoul Mustermann",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        TextButton(onClick = { /* TODO: Edit Profile */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.size(4.dp))
                            Text("Profil bearbeiten", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Spacer(modifier = Modifier.size(32.dp))
            }

            item {
                // account settings
                SectionCard (title = "Account") {
                    MenuItem(text = "E-Mail hinzufügen", onClick = { /* TODO */ })
                    Divider()
                    MenuItem(text = "Passwort ändern", onClick = { /* TODO */ })
                    Divider()
                    MenuItem(text = "Zwei-Faktor-Authentifizierung", onClick = { /* TODO */ })
                }
                Spacer(modifier = Modifier.size(24.dp))
            }

            item {
                // other
                SectionCard(title = "Mehr") {
                    MenuItem(
                        text = "Benachrichtigungen",
                        onClick = { showNotifDialog = true }
                    )
                    Divider()
                    MenuItem(text = "Hilfe & Support", onClick = { navController.navigate(Screen.HelpSupportScreen.route) })
                }
                Spacer(modifier = Modifier.size(24.dp))
            }

            item {
                // Logout
                Text(
                    modifier = Modifier
                        .clickable { navController.navigate(Screen.LoginScreen.route) }
                        .padding(8.dp),
                    text = "Abmelden",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        if (showNotifDialog) {
            NotificationsDialog(
                enabled = false,                                // TODO: load value from settigs
                onEnabledChange = { /* TODO: speichern */ },
                onDismiss = { showNotifDialog = false }
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(MaterialTheme.colorScheme.surface),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            content()
        }
    }
}

@Composable
private fun MenuItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}


@Composable
private fun NotificationsDialog(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Benachrichtigungen") },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Aktivieren", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fertig")
            }
        }
    )
}


@Composable
fun HelpSupportScreen(modifier: Modifier = Modifier, navController: NavController) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            // Back arrow
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Zurück",
                    modifier = Modifier
                        .clickable(onClick = { navController.popBackStack() })
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Hilfe & Support",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
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
}
