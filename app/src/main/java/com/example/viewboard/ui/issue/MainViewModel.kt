package com.example.viewboard.ui.issue

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueState
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.components.homeScreen.IssueProgress
import com.example.viewboard.components.homeScreen.IssueProgressCalculator
import com.example.viewboard.components.homeScreen.TimeSpanFilter
import com.example.viewboard.components.homeScreen.next
import com.example.viewboard.ui.screens.ProjectFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel : ViewModel() {
    val items = mutableStateListOf<IssueLayout>()
    val Project = mutableStateListOf<ProjectLayout>()
    val Views = mutableStateListOf<ViewLayout>()

    var isDragging by mutableStateOf(false)
        private set
    // 1) Eingabe‑State für Filter und Zeitspanne
    private val _filterMode = MutableStateFlow(ProjectFilter.ALL)
    private val _timeSpan   = MutableStateFlow(TimeSpanFilter.CURRENT_MONTH)
    val timeSpan: StateFlow<TimeSpanFilter> = _timeSpan.asStateFlow()
    // 2) Ausgabe‑State für den Fortschritt
    private val _progress = MutableStateFlow(IssueProgress(0, 0, 0.0f))
    val progress: StateFlow<IssueProgress> = _progress.asStateFlow()

    private val calculator = IssueProgressCalculator()
    private val _projectProgress = MutableStateFlow(IssueProgress(0,0,0f))
    val projectProgress: StateFlow<IssueProgress> = _projectProgress.asStateFlow()

    fun observeProject(projectId: String, timeSpan: TimeSpanFilter) {
        viewModelScope.launch {
            calculator
                .getProjectProgressFlow(projectId, timeSpan)
                .collectLatest { progress ->
                    _projectProgress.value = progress
                }
        }
    }

    private val _viewsFlow: StateFlow<List<ViewLayout>> =
        FirebaseAPI
            .getAllViews()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    val views: StateFlow<List<ViewLayout>> = _viewsFlow



    private val _selectedViewId = MutableStateFlow<String?>(null)
    val selectedViewId: StateFlow<String?> = _selectedViewId.asStateFlow()

    fun selectView(id: String) {
        _selectedViewId.value = id
    }
    init {
        // 3️⃣ Nur beim ersten Mal, wenn views non‑empty wird, setze den ersten Wert
        viewModelScope.launch {
            _viewsFlow
                .filter { it.isNotEmpty() }    // warte bis Liste nicht mehr leer ist
                .first()                        // nimm nur die allererste Emission
                .let { nonEmptyList ->
                    _selectedViewId.value = nonEmptyList.first().id
                }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val issuesForSelectedView: StateFlow<List<IssueLayout>> =
        _selectedViewId
            .filterNotNull()
            .flatMapLatest { viewId ->
                FirebaseAPI.getIssuesFromView(viewId)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )


    init {
        viewModelScope.launch {
            _timeSpan
                .flatMapLatest { span ->
                    calculator.getProgressFlow(span)
                }
                .collectLatest { issueProgress ->
                    _progress.value = issueProgress
                }
        }
    }
    fun advanceTimeSpan() {
        _timeSpan.value = _timeSpan.value.next()
    }

    fun advanceTimeSpan(span: TimeSpanFilter) {
        val nextSpan = span.next()
        _timeSpan.value = nextSpan
    }

    var issues = mutableStateListOf<IssueLayout>()
    fun loadMyProjectFromUser() {
        viewModelScope.launch {
            FirebaseAPI.getProjectsFromUser(AuthAPI.getUid()).collectLatest { ProjectList ->
                Project.clear()
                Project .addAll(ProjectList)
            }
        }
    }


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
    fun loadViews() {
        viewModelScope.launch {
            FirebaseAPI.getAllViews().collectLatest { issueList ->
                Views.clear()
                Views .addAll(issueList)
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

