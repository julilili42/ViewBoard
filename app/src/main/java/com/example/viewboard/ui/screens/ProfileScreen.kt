package com.example.viewboard.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.viewboard.R
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.auth.impl.FirebaseProvider
import com.example.viewboard.components.homeScreen.ProfilePicture
import com.example.viewboard.ui.navigation.Screen
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.viewboard.components.profile.SectionCard
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext


@Composable
fun ProfileScreen(modifier: Modifier = Modifier, navController: NavController) {
    var showNotifDialog by remember { mutableStateOf(false) }
    val userName = AuthAPI.getCurrentDisplayName() ?: "failed to load username"
    val uid = FirebaseProvider.auth.currentUser?.uid
    var notificationsEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
        }
    )


    LaunchedEffect(Unit) {
        uid?.let {
            Firebase.firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    val value = document.getBoolean("notificationsEnabled") ?: false
                    notificationsEnabled = value
                }
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background, // hier die Surface‑Farbe
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = { AuthAPI.logout(navController) },
                    modifier = Modifier
                        .height(40.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Log out",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }

        }
    ) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            item {
                // profile image
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        ProfilePicture(
                            painter = painterResource(id = R.drawable.logotest),
                            contentDescription = "pb Raoul",
                            size = 120.dp,
                            borderColor = MaterialTheme.colorScheme.primary,
                            borderWidth = 3.dp
                        )

                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.size(32.dp))
            }

            item {
                // account settings
                SectionCard(title = "Account") {
                    MenuItem(
                        text = "Change E-Mail",
                        onClick = { navController.navigate(Screen.ChangeEmailScreen.route) })
                    Divider()
                    MenuItem(
                        text = "Change Password",
                        onClick = { navController.navigate(Screen.ChangePasswordScreen.route) })
                }
                Spacer(modifier = Modifier.size(24.dp))
            }

            item {
                SectionCard(title = "More") {
                    MenuItem(
                        text = "Notifications",
                        onClick = { showNotifDialog = true }
                    )
                    Divider()
                    MenuItem(
                        text = "Help & Support",
                        onClick = { navController.navigate(Screen.HelpSupportScreen.route) })
                }
            }
        }
        if (showNotifDialog) {
            NotificationsDialog(
                enabled = notificationsEnabled,
                onEnabledChange = { newValue ->
                    notificationsEnabled = newValue

                    if (newValue && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permissionGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!permissionGranted) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                    uid?.let {
                        Firebase.firestore.collection("users")
                            .document(it)
                            .update("notificationsEnabled", newValue)
                    }
                },
                onDismiss = { showNotifDialog = false }
            )
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
        title = { Text("Notifications") },
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Activate", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}



