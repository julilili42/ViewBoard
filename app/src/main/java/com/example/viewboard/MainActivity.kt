package com.example.viewboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.Navigation
import com.example.viewboard.ui.theme.ComposeLoginScreenInitTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAPI.init()
        enableEdgeToEdge()


        lifecycleScope.launch {
            val projects = FirebaseAPI.getProjectsFromUser(AuthAPI.getUid())
            projects.collect { list ->
                list.forEach { i -> println(i) }
            }
        }

        lifecycleScope.launch {
            val projects = FirebaseAPI.getAllProjects()
            projects.collect { list ->
                list.forEach { i -> println(i) }
            }
        }

        setContent {
            ComposeLoginScreenInitTheme {
                Navigation()
            }
        }

    }
}