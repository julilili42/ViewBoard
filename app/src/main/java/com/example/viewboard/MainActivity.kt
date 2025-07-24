package com.example.viewboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.viewboard.backend.notification.impl.Notification
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.Navigation
import com.example.viewboard.ui.theme.ComposeLoginScreenInitTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        FirebaseAPI.init()
        enableEdgeToEdge()
        AuthAPI.ensureUserProfileExists(
            onSuccess = {},
            onError = {}
        )
        Notification.createNotificationChannel(this)

        AuthAPI.ensureUserProfileExists(
            onSuccess = {
                lifecycleScope.launch {
                    Notification.checkUpcomingDeadlines(this@MainActivity)
                    Notification.checkNewIssueAssignments(this@MainActivity)
                    Notification.checkNewProjectAssignments(this@MainActivity)                }
            },
            onError = { /* TODO error handling */ }
        )

        setContent {
            ComposeLoginScreenInitTheme {
                Navigation()
            }
        }

    }
}