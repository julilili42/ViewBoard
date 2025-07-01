package com.example.viewboard.ui.navigation

import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun hasSoftNavigationBar(): Boolean {
    val res = LocalContext.current.resources
    val id = res.getIdentifier("config_showNavigationBar", "bool", "android")

    val hasSoftKeys = if (id > 0) {
        res.getBoolean(id)
    } else {
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        val hasMenuKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_MENU)
        !(hasBackKey || hasMenuKey)
    }
    return hasSoftKeys
}


