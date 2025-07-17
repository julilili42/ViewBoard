package com.example.viewboard

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
        FirebaseAPI.init()
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)

        lifecycleScope.launch {
            AuthAPI.getListOfAllUsers().onSuccess { users ->
                // Hier hast du deine Liste
                users.forEach { user ->
                    Log.d("USERS", "${user.uid}: ${user.name} (${user.email})")
                }
            }.onFailure { e ->
                Log.e("USERS", "Fehler: ${e.message}")
            }
        }

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