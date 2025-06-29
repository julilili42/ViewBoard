package com.example.viewboard.dataclass

import androidx.compose.ui.graphics.Color

data class Project(
    val name: String,
    val description: String,
    val phase: String,
    val startMonth: Int, // 1–12
    val endMonth: Int,   // 1–12
    val color: Color,
    val totalMilestones: Int,
    val completedMilestones: Float
) {

}

