package com.example.viewboard

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.viewboard.backend.NotificationHelper
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
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
        NotificationHelper.createNotificationChannel(this)

        lifecycleScope.launch {
            NotificationHelper.checkUpcomingDeadlines(this@MainActivity)
            NotificationHelper.checkNewIssueAssignments(this@MainActivity)
            NotificationHelper.checkNewProjectAssignments(this@MainActivity)
        }


        setContent {
            ComposeLoginScreenInitTheme {
                Navigation()
            }
        }

    }
}