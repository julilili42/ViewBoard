package com.example.viewboard.frontend.stateholder
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.IssueDeadlineFilter
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueProgress
import com.example.viewboard.backend.dataLayout.IssueState
import com.example.viewboard.frontend.components.home.issueProgress.IssueProgressCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged

class IssueViewModel : ViewModel() {
    enum class SortField { DATE, NAME }
    enum class SortOrder { ASC, DESC }



    private val _allIssues = MutableStateFlow<List<IssueLayout>>(emptyList())
    val allIssues: StateFlow<List<IssueLayout>> = _allIssues.asStateFlow()
    private val _filter = MutableStateFlow<IssueState?>(null)
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    private val _sortField = MutableStateFlow(SortField.DATE)
    val sortField: StateFlow<SortField> = _sortField.asStateFlow()
    private val _sortOrder = MutableStateFlow(SortOrder.ASC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()
    private val _showOnlyMyIssues  = MutableStateFlow(false)
    val showOnlyMyIssues: StateFlow<Boolean> = _showOnlyMyIssues.asStateFlow()

    private val userId = AuthAPI.getUid() ?: ""
    private val _projectId = MutableStateFlow<String?>(null)
    private val projectId: StateFlow<String?> = _projectId
    private val _viewId = MutableStateFlow<String?>(null)

    private val _items = mutableStateListOf<IssueLayout>()
    val items: List<IssueLayout> get() = _items

    private val _timeSpan   = MutableStateFlow(IssueDeadlineFilter.CURRENT_MONTH)
    private val _progress = MutableStateFlow(IssueProgress(0, 0, 0.0f))
    private val calculator = IssueProgressCalculator()

    private val _emailsForIssue = MutableStateFlow<Map<String, List<String?>>>(emptyMap())
    val emailsForIssue: StateFlow<Map<String, List<String?>>> = _emailsForIssue
    private val _issuesForSelectedProject = MutableStateFlow<List<IssueLayout>>(emptyList())
    val issuesForSelectedProject: StateFlow<List<IssueLayout>> = _issuesForSelectedProject
    private val _selectedProjectId = MutableStateFlow<String?>(null)
    val selectedProjectId: StateFlow<String?> = _selectedProjectId
    private val _state = MutableStateFlow<IssueState>(IssueState.NEW)
    val state: StateFlow<IssueState> = _state
    var isDragging by mutableStateOf(false)

    

    /** Lade Issues aus Firebase entsprechend dem Projekt */
    fun reload() {
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromProject(projectId.toString())
                .collectLatest { _allIssues.value = it }
        }
    }
    private fun reloadForProject(id: String) {
        viewModelScope.launch {
            FirebaseAPI
                .getIssuesFromProject(id)
                .collectLatest { _allIssues.value = it }
        }
    }

    fun setState(newState: IssueState) {
        _state.value = newState
    }

    /** Setze den Issue‑Filter */
    fun setFilter(state: IssueState?) {
        _filter.value = state
        reload()
    }
    fun setShowOnlyMine() {
        _showOnlyMyIssues.value = !_showOnlyMyIssues.value
    }
    /** Setze die aktuelle Project‑ID */
    fun setProject(projectId: String) {
        _projectId.value = projectId
    }

    /** Setze die Such‑Query */
    fun setQuery(q: String) {
        _query.value = q
    }

    /** Setze Sortierfeld (Datum oder Alphabet) */
    fun setSortField(field: SortField) {
        _sortField.value = field
    }

    /** Setze Sortierreihenfolge (auf- oder absteigend) */
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun loadIssuesFromView(viewID: String) {
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromView(viewID)
                .collectLatest { list ->
                    _allIssues.value = list
                }
        }
    }

    fun startDragging(){
        isDragging = true }

    fun stopDragging(){
        isDragging = false }

    fun moveItemToState(item: IssueLayout, newState: IssueState) {
        if (item.state == newState) return
        item.state = newState
        FirebaseAPI.updIssue(item)
    }

    fun setCurrentViewId(viewId: String) {
        _viewId.value = viewId
    }

    fun selectProject(projectId: String) {
        _selectedProjectId.value = projectId
    }

    fun loadIssuesForProject() {
        selectedProjectId.value?.let { projectId ->
            viewModelScope.launch {
                FirebaseAPI.getIssuesFromProject(projectId)
                    .collectLatest { list ->
                        _issuesForSelectedProject.value = list
                    }
            }
        }
    }

    val issuesFlow: Flow<List<IssueLayout>> = _viewId
        .filterNotNull()
        .flatMapLatest { viewId ->
            if (viewId.isBlank()) {
                flowOf(emptyList())
            } else {
                FirebaseAPI.getIssuesFromView(
                    viewID    = viewId,
                    onSuccess = { },
                    onFailure = { }
                )
            }
        }

    private val rawAllIssuesFlow: Flow<List<IssueLayout>> =
        _state
            .flatMapLatest { stateFilter ->
                FirebaseAPI
                    .getIssuesFromUser(userId)
                    .map { list ->
                        stateFilter
                            ?.let { s -> list.filter { it.state == s } }
                            ?: list
                    }
            }

    val displayedIssuesFromViews: StateFlow<List<IssueLayout>> = combine(
        issuesFlow,
        _filter,
        _query,
        _sortField,
        _sortOrder
    ) { list, filterState, q, sortField, sortOrder ->
        val byState = filterState
            ?.let { s -> list.filter { it.state == s } }
            ?: list

        val byQuery = if (q.isBlank()) byState
        else byState.filter { it.title.contains(q, ignoreCase = true) }
        byQuery.sortedWith(
            compareBy<IssueLayout> {
                when (sortField) {
                    SortField.DATE -> it.deadlineTS
                    SortField.NAME -> it.title.lowercase()
                }
            }.let { cmp ->
                if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp
            }
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val rawIssuesFlow: Flow<List<IssueLayout>> =
        combine(
            _projectId.filterNotNull(),
            _showOnlyMyIssues,
            _state
        ) { projId, onlyMine, stateFilter ->
            Triple(projId, onlyMine, stateFilter)
        }
            .flatMapLatest { (projId, onlyMine, stateFilter) ->
                // 1) Hol die Issues
                val sourceFlow: Flow<List<IssueLayout>> = FirebaseAPI.getIssuesFromProject(projId)

                    .map { list ->
                        if (onlyMine) {
                            list.filter { issue ->
                                issue.users.contains(userId)
                            }
                        } else {
                            list
                        }
                    }
                sourceFlow.map { list ->
                    stateFilter
                        .let { s -> list.filter { it.state == s } }
                        ?: list
                }
            }

    val displayedIssues: StateFlow<List<IssueLayout>> = combine(
        rawIssuesFlow,
        _filter,
        _query,
        _sortField,
        _sortOrder
    ) { list, filterState, q, sortField, sortOrder ->
        val byState = filterState
            ?.let { s -> list.filter { it.state == s } }
            ?: list

        val byQuery = if (q.isBlank()) byState
        else byState.filter { it.title.contains(q, ignoreCase = true) }

        byQuery.sortedWith(
            compareBy<IssueLayout> {
                when (sortField) {
                    SortField.DATE -> it.creationTS
                    SortField.NAME -> it.title.lowercase()
                }
            }.let { cmp ->
                if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp
            }
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )



    val displayedAllIssues: StateFlow<List<IssueLayout>> =
        combine(
            rawAllIssuesFlow,
            _query,
            _sortField,
            _sortOrder
        ) { list, q, sortField, sortOrder ->
            val byQuery = if (q.isBlank()) list
            else list.filter { it.title.contains(q, ignoreCase = true) }
            byQuery.sortedWith(
                compareBy<IssueLayout> {
                    when (sortField) {
                        SortField.DATE -> it.creationTS
                        SortField.NAME -> it.title.lowercase()
                    }
                }.let { cmp ->
                    if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp
                }
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            projectId.filterNotNull().collectLatest { id ->
                FirebaseAPI
                    .getIssuesFromProject(id)
                    .collectLatest { list ->
                        _allIssues.value = list
                    }
            }
        }
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
            _projectId
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { newProjId ->
                    reloadForProject(newProjId)
                }
        }
        viewModelScope.launch {
            displayedIssues
                .collectLatest { issues ->
                    val pairs = coroutineScope {
                        issues.map { issue ->
                            async {
                                val mails = runCatching {
                                    AuthAPI.getEmailsByIds(issue.users)
                                }.getOrNull()?.getOrNull() ?: emptyList()
                                issue.id to mails
                            }
                        }.awaitAll()
                    }
                    _emailsForIssue.value = pairs.toMap() }
        }
    }
}
