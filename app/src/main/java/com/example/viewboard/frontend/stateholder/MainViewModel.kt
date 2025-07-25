package com.example.viewboard.frontend.stateholder

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viewboard.backend.dataLayout.IssueDeadlineFilter
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueProgress
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.frontend.components.home.issueProgress.IssueProgressCalculator
import com.example.viewboard.frontend.components.home.issueProgress.next
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel : ViewModel() {
    val items = mutableStateListOf<IssueLayout>()
    private val _timeSpan = MutableStateFlow(IssueDeadlineFilter.CURRENT_MONTH)
    val timeSpan: StateFlow<IssueDeadlineFilter> = _timeSpan.asStateFlow()
    private val _progress = MutableStateFlow(IssueProgress(0, 0, 0.0f))
    val progress: StateFlow<IssueProgress> = _progress.asStateFlow()
    private val calculator = IssueProgressCalculator()
    private val _selectedViewId = MutableStateFlow<String?>(null)
    var issues = mutableStateListOf<IssueLayout>()

    private val _viewsFlow: StateFlow<List<ViewLayout>> =
        FirebaseAPI
            .getAllViews()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    val views: StateFlow<List<ViewLayout>> = _viewsFlow

    fun advanceTimeSpan() {
        _timeSpan.value = _timeSpan.value.next()
    }

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
        viewModelScope.launch {
            _viewsFlow
                .filter { it.isNotEmpty() }
                .first()
                .let { nonEmptyList ->
                    _selectedViewId.value = nonEmptyList.first().id
                }
        }
    }
}

