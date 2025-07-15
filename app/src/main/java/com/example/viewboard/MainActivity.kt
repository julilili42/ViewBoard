package com.example.viewboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.navigation.Navigation
import com.example.viewboard.ui.theme.ComposeLoginScreenInitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAPI.init()
        enableEdgeToEdge()

        setContent {
            ComposeLoginScreenInitTheme {
                Navigation()
            }
        }

    }
}