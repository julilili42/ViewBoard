package com.example.viewboard.ui.issue

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color


class MainViewModel : ViewModel() {
    val items = mutableStateListOf<IssueUiItem>()
    var isDragging by mutableStateOf(false)
        private set

    init {
        items.addAll(
            listOf(
                IssueUiItem(
                    title = "Dashboard design for admin",
                    priority = "High",
                    status = "On Track",
                    date = "14 Oct 2022",
                    attachments = 5,
                    comments = 5,
                    assignees = listOf("MA", "LA", "MC"),
                    backgroundColor = Color.Gray
                ),
                IssueUiItem(
                    title = "User login flow",
                    priority = "Medium",
                    status = "Stuck",
                    date = "20 Oct 2022",
                    attachments = 3,
                    comments = 2,
                    assignees = listOf("AL", "BR"),
                    backgroundColor = Color.Blue
                ),
                IssueUiItem(
                    title = "Payment integration",
                    priority = "Low",
                    status = "Done",
                    date = "10 Oct 2022",
                    attachments = 2,
                    comments = 1,
                    assignees = listOf("CH"),
                    backgroundColor = Color.Green
                )
            )
        )
    }


    fun startDragging() { isDragging = true }
    fun stopDragging()  { isDragging = false }

    fun moveItemToCategory(item: IssueUiItem, category: Int) {
        if (item.category == category) return
        item.category = category
    }

    fun getItemsForCategory(category: Int): List<IssueUiItem> {
        return items.filter { it.category == category }
    }
}

