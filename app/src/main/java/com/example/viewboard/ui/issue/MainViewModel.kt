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
import com.example.viewboard.backend.dataLayout.IssueState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val items = mutableStateListOf<IssueLayout>()
    var isDragging by mutableStateOf(false)
        private set

    var issues = mutableStateListOf<IssueLayout>()
        private set

    fun loadMyIssues(projectId: String) {
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromUser(AuthAPI.getUid(), projectId).collectLatest { issueList ->
                items.clear()
                items .addAll(issueList)
            }
        }
    }

    fun loadIssuesFromView(viewID: String) {
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromView(viewID).collectLatest { issueList ->
                items.clear()
                items .addAll(issueList)
            }
        }
    }

    fun loadAllIssues(projectId: String) {
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromProject(projectId).collectLatest { issueList ->
                items.clear()
                items .addAll(issueList)
            }
        }
    }

    fun startDragging() { isDragging = true }
    fun stopDragging()  { isDragging = false }

    /*fun moveItemToCategory(item: IssueUiItem, category: Int) {
        if (item.category == category) return
        item.category = category
    }*/
    fun moveItemToState(item: IssueLayout, newState: IssueState) {
        if (item.state == newState) return
        item.state = newState
        FirebaseAPI.updIssue(item)
    }
    fun getItemsForCategory(state: IssueState): List<IssueLayout> {
        return items.filter { it.state == state }
    }



}

