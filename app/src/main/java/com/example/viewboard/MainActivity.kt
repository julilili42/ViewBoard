package com.example.viewboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.Navigation
import com.example.viewboard.ui.theme.ComposeLoginScreenInitTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAPI.init()
        lifecycleScope.launch {
            val projects = FirebaseAPI.getAllProjects()
            projects.collect { list ->
                list.forEach { i -> println(i) }
            }
        }
        enableEdgeToEdge()

        setContent {
            ComposeLoginScreenInitTheme {
                Navigation()
            }
        }

    }
}