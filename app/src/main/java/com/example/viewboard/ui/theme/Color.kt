package com.example.viewboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
val Black = Color(0xFF000113)
val BlueGray = Color(0xFF334155)


@Composable
fun uiColor(): Color =
    if (isSystemInDarkTheme()) Color.White
    else Black
