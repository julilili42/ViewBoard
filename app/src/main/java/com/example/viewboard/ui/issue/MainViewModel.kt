package com.example.viewboard.ui.issue

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch



class MainViewModel : ViewModel() {
    val items = mutableStateListOf<IssueUiItem>()
    var isDragging by mutableStateOf(false)
        private set

    var issues = mutableStateListOf<IssueLayout>()
        private set

    fun loadMyIssues(projectId: String) {
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromUser(AuthAPI.getUid(), projectId).collectLatest { issueList ->
                issues.clear()
                issues.addAll(issueList)

                items.clear()
                items.addAll(
                    issueList.map { issue ->
                        IssueUiItem(
                            title = issue.title,
                            priority = "–",
                            status = issue.state.name,
                            date = issue.deadlineTS.toString(),
                            attachments = 0,
                            comments = 0,
                            assignees = issue.assignments,
                            backgroundColor = Color.Gray,
                            id = issue.id
                        )
                    }
                )
            }
        }
    }

    fun loadAllIssues(projectId: String) {
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromProject(projectId).collectLatest { issueList ->
                issues.clear()
                issues.addAll(issueList)

                items.clear()
                items.addAll(
                    issueList.map { issue ->
                        IssueUiItem(
                            title = issue.title,
                            priority = "–",
                            status = issue.state.name,
                            date = issue.deadlineTS.toString(),
                            attachments = 0,
                            comments = 0,
                            assignees = issue.assignments,
                            backgroundColor = Color.Gray,
                            id = issue.id
                        )
                    }
                )
            }
        }
    }


    private fun issueLayoutToUiItem(issue: IssueLayout): IssueUiItem {
        return IssueUiItem(
            title = issue.title,
            priority = "Medium",
            status = issue.state.name,
            date = issue.deadlineTS.toString(),
            attachments = 0,
            comments = 0,
            assignees = issue.assignments,
            backgroundColor = Color.Gray // ggf. aus Label ableiten
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

