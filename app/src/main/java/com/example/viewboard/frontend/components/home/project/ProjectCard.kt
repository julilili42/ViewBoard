package com.example.viewboard.frontend.components.home.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A card representing a single project in the grid.
 *
 * @param name The display name of the project
 * @param onClick Action to perform when the card is clicked
 */
@Composable
fun ProjectCard(
    name: String,
    count: Int = 0,
    onClick: () -> Unit
) {
    val gradientColors = remember { randomGradientColors() }

    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.linearGradient(colors = gradientColors))
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


fun randomGradientColors(): List<Color> {
    val colors = listOf(
        Color(0xFFE57373), // Rot
        Color(0xFF64B5F6), // Blau
        Color(0xFF81C784), // Grün
        Color(0xFFFFB74D), // Orange
        Color(0xFFBA68C8), // Lila
        Color(0xFFFF8A65), // Koralle
        Color(0xFFA1887F), // Braun
        Color(0xFF4DB6AC), // Türkis
    )
    return listOf(
        colors.random(),
        colors.filterNot { it == colors.first() }.random()
    )
}
