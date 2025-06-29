package com.example.viewboard.ui.issue
import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.*

import java.util.UUID


data class IssueUiItem(
    val title: String,
    val priority: String,
    val status: String,
    val date: String,
    val attachments: Int,
    val comments: Int,
    val assignees: List<String>, // URLs or initials
    val backgroundColor: Color,
    val id: String = UUID.randomUUID().toString(),
    val initialCategory: Int = 0
) {
    var category by mutableStateOf(initialCategory)
}
