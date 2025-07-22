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
import kotlinx.coroutines.flow.Flow
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
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.distinctUntilChanged

class IssueViewModel : ViewModel() {

    companion object {
        private const val TAG = "IssueViewModel"
    }
    // Aktuelle User‑ID
    private val myId = AuthAPI.getUid() ?: ""

    // Alle geladenen Issues
    private val _allIssues = MutableStateFlow<List<IssueLayout>>(emptyList())

    // Filter: spezieller IssueState oder null für alle
    private val _filter = MutableStateFlow<IssueState?>(null)

    // Such‑Query
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Sortierkriterien
    enum class SortField { DATE, NAME }
    enum class SortOrder { ASC, DESC }
    // Aktuelle Project-ID
    private val _projectId = MutableStateFlow<String?>(null)
    private val _viewId = MutableStateFlow<String?>(null)
    private val projectId: StateFlow<String?> = _projectId

    private val _sortField = MutableStateFlow(SortField.DATE)
    private val _sortOrder = MutableStateFlow(SortOrder.ASC)
    private val _showOnlyMyIssues  = MutableStateFlow(false)
    val showOnlyMyIssues: StateFlow<Boolean> = _showOnlyMyIssues.asStateFlow()
    val sortField: StateFlow<SortField> = _sortField.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()
    private val _items = mutableStateListOf<IssueLayout>()
    val items: List<IssueLayout> get() = _items

    private val _filterState       = MutableStateFlow<IssueState?>(null)
    // Kombiniere Filter-, Such- und Sortfeld-States
    private val _filtered = combine(
        _allIssues,
        _filter,
        _query,
        _sortField
    ) { list, filterState, q, sortField ->
        // Filter nach IssueState
        val filteredByState = filterState?.let { state ->
            list.filter { it.state == state }
        } ?: list
        // Suche nach Titel
        val filteredByQuery = if (q.isBlank()) filteredByState else filteredByState.filter {
            it.title.contains(q, ignoreCase = true)
        }
        Pair(filteredByQuery, sortField)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Pair(emptyList(), SortField.DATE)
    )
    private val _state = MutableStateFlow<IssueState>(IssueState.NEW)
    fun setState(newState: IssueState) {
        _state.value = newState
    }
    // 2) öffentlicher, readonly State für die UI
    val state: StateFlow<IssueState> = _state

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
                                issue.assignments.contains(myId)
                            }
                        } else {
                            list
                        }
                    }
                // 2) Filtere nach IssueState, falls stateFilter != null
                sourceFlow.map { list ->
                    stateFilter
                        .let { s -> list.filter { it.state == s } }
                        ?: list
                }
            }

    // 2) Darauf aufbauend kombiniere alle Filter‑/Such‑/Sort‑Inputs
    val displayedIssues: StateFlow<List<IssueLayout>> = combine(
        rawIssuesFlow,
        _filter,
        _query,
        _sortField,
        _sortOrder
    ) { list, filterState, q, sortField, sortOrder ->
        // a) IssueState‑Filter
        val byState = filterState
            ?.let { s -> list.filter { it.state == s } }
            ?: list

        // b) Such‑Filter
        val byQuery = if (q.isBlank()) byState
        else byState.filter { it.title.contains(q, ignoreCase = true) }

        // c) Sortierung
        byQuery.sortedWith(
            compareBy<IssueLayout> {
                when (sortField) {
                    SortField.DATE  -> it.creationTS
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

    /** Setze den Issue‑Filter */
    fun setFilter(state: IssueState?) {
        _filter.value = state
        reload()
    }

    fun setShowOnlyMine() {
        _showOnlyMyIssues.value = !_showOnlyMyIssues.value
    }
    /** Wechsle Sortierung: Feld und Richtung */
    fun toggleSort(field: SortField) {
        if (_sortField.value == field) {
            _sortOrder.value = if (_sortOrder.value == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC
        } else {
            _sortField.value = field
            _sortOrder.value = SortOrder.ASC
        }
    }

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

    init {
        viewModelScope.launch {
            _projectId
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest { newProjId ->
                    reloadForProject(newProjId)
                }
        }
    }



    // Toggle: nur eigene Issues (privat) oder global
    private val _showOnlyMine = MutableStateFlow(true)
    val showOnlyMine: StateFlow<Boolean> = _showOnlyMine.asStateFlow()

    // Filter: nach Labels (Leere Liste = keine Filterung)
    private val _labelFilter = MutableStateFlow<Set<String>>(emptySet())
    val labelFilter: StateFlow<Set<String>> = _labelFilter.asStateFlow()

    // Gewünschter Issue-Zustand (Tab)
    private val _selectedState = MutableStateFlow(IssueState.NEW)
    val selectedState: StateFlow<IssueState> = _selectedState.asStateFlow()

    /**
     * Kombinierter Flow: Filtert und sortiert Issues basierend auf allen Kriterien.
     */
    // 1) Basiskombination ohne SortOrder: Filterung und SortField

    /** Setze die aktuelle Project‑ID */
    fun setProject(projectId: String) {
        _projectId.value = projectId
    }

    /** Toggle: nur eigene Issues */
    fun setShowOnlyMine(onlyMine: Boolean) {
        _showOnlyMine.value = onlyMine
    }

    /** Setze Label-Filter */
    fun setLabelFilter(labels: Set<String>) {
        _labelFilter.value = labels
    }

    /** Setze den Tab‑Zustand */
    fun setSelectedState(state: IssueState) {
        _selectedState.value = state
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
    /** Aktionen zum Setzen der Filter & Selektionen */


    fun setTimeSpan(span: TimeSpanFilter){ _timeSpan.value   = span }

    /** Initialisiere Laden anhand projectId */
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
    }

    val Project = mutableStateListOf<ProjectLayout>()
    val Views = mutableStateListOf<ViewLayout>()
    private val _issues = MutableStateFlow<List<IssueLayout>>(emptyList())
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
                _items.clear()
                _items .addAll(issueList)

            }
        }
    }

    fun loadIssuesFromView(viewID: String) {
        Log.d(TAG, "loadIssuesFromView: start loading issues for viewID=$viewID")
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromView(viewID)
                .collectLatest { list ->
                    Log.d(
                        TAG,
                        "loadIssuesFromView: loaded ${list.size} issues for viewID=$viewID"
                    )
                    _allIssues.value = list
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
                _items.clear()
                _items .addAll(issueList)
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
        return _items.filter { it.state == state }
    }

    fun getIssuesFlowForView(
        viewId: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ): Flow<List<IssueLayout>> {
        return FirebaseAPI.getIssuesFromView(
            viewID    = viewId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
    fun setCurrentViewId(viewId: String) {
        _viewId.value = viewId
        Log.d(TAG, "Current viewId set to: $viewId")
    }
    val issuesFlow: Flow<List<IssueLayout>> = _viewId
        .filterNotNull()
        .flatMapLatest { viewId ->
            if (viewId.isBlank()) {
                Log.w(TAG, "getIssuesForCurrentView called before setting viewId")
                flowOf(emptyList())
            } else {
                FirebaseAPI.getIssuesFromView(
                    viewID    = viewId,
                    onSuccess = { Log.d(TAG, "Issues for view loaded: $viewId") },
                    onFailure = { Log.e(TAG, "Failed to load view issues: $viewId") }
                )
            }
        }
    val displayedIssuesFromViews: StateFlow<List<IssueLayout>> = combine(
        issuesFlow,
        _filter,
        _query,
        _sortField,
        _sortOrder
    ) { list, filterState, q, sortField, sortOrder ->
        // a) IssueState‑Filter
        val byState = filterState
            ?.let { s -> list.filter { it.state == s } }
            ?: list

        // b) Such‑Filter
        val byQuery = if (q.isBlank()) byState
        else byState.filter { it.title.contains(q, ignoreCase = true) }

        // c) Sortierung
        byQuery.sortedWith(
            compareBy<IssueLayout> {
                when (sortField) {
                    SortField.DATE  -> it.deadlineTS
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
    private val _issuesForSelectedProject = MutableStateFlow<List<IssueLayout>>(emptyList())
    val issuesForSelectedProject: StateFlow<List<IssueLayout>> = _issuesForSelectedProject

    private val _selectedProject = MutableStateFlow<String?>(null)
    val selectedProject: StateFlow<String?> = _selectedProject
    fun selectProject(projectId: String) {
        _selectedProject.value = projectId
    }
    fun loadIssuesForProject() {
        selectedProject.value?.let { projectId ->
            viewModelScope.launch {
                FirebaseAPI.getIssuesFromProject(projectId)
                    .collectLatest { list ->
                        _issuesForSelectedProject.value = list
                    }
            }
        }
    }

    private val rawAllIssuesFlow: Flow<List<IssueLayout>> =
        _state
            // Jedes Mal, wenn sich der State‑Filter ändert, holen wir den Nutzer‑Flow neu
            .flatMapLatest { stateFilter ->
                FirebaseAPI
                    .getIssuesFromUser(myId)             // ← jetzt user‑bezogen
                    .map { list ->
                        // 1) State‑Filter anwenden, falls nicht null
                        stateFilter
                            ?.let { s -> list.filter { it.state == s } }
                            ?: list
                    }
            }

    // 4) Finaler Flow: zusätzlich Such‑ und Sortier‑Logik
    val displayedAllIssues: StateFlow<List<IssueLayout>> =
        combine(
            rawAllIssuesFlow,
            _query,
            _sortField,
            _sortOrder
        ) { list, q, sortField, sortOrder ->
            // a) Such‑Filter
            val byQuery = if (q.isBlank()) list
            else list.filter { it.title.contains(q, ignoreCase = true) }

            // b) Sortierung
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

}
