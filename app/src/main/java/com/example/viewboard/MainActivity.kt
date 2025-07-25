package com.example.viewboard

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.viewboard.backend.notification.impl.Notification
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.frontend.navigation.Navigation
import com.example.viewboard.frontend.components.theme.ComposeLoginScreenInitTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // custom start-up screen with app icon
        installSplashScreen()

        // initializes local references to data base
        FirebaseAPI.init()

        // use full screen size
        enableEdgeToEdge()

        // created to show/enable notifications
        Notification.createNotificationChannel(this)

        // notification checks
        AuthAPI.ensureUserProfileExists(
            onSuccess = {
                lifecycleScope.launch {
                    Notification.checkUpcomingDeadlines(this@MainActivity)
                    Notification.checkNewIssueAssignments(this@MainActivity)
                    Notification.checkNewProjectAssignments(this@MainActivity)                }
            },
            onError = { error ->
                Log.e("MainActivity", "profile could not be established")
                Toast.makeText(
                    this@MainActivity,
                    "Error when loading profile",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        setContent {
            // color theme
            ComposeLoginScreenInitTheme {
                // app navigation-graph
                Navigation()
            }
        }

    }
}