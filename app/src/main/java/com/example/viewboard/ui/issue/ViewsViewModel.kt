package com.example.viewboard.ui.issue


import android.util.Log
import kotlinx.coroutines.flow.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.issue.ProjectViewModel.SortField
import com.example.viewboard.ui.issue.ProjectViewModel.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch


class ViewsViewModel : ViewModel() {
    // RAW: Flow aller Views aus Firebase
    // RAW: Flow aller Views aus Firebase
    private val myId: String = AuthAPI.getUid() ?: ""
    val viewFlowview: Flow<List<ViewLayout>> = FirebaseAPI.getAllViews()
        .map { views -> views.filter { it.creator == myId } }
    val viewFlowHome: Flow<List<ViewLayout>> = FirebaseAPI.getAllViews()
        .map { views ->
            views.filter { view ->
                view.creator == myId && view.issues.isNotEmpty()  // nur Views mit mindestens einem Issue
            }
        }
    // StateFlows für Filter, Suche und Sortierung
    private val _filter = MutableStateFlow<String?>(null)
    private val _query = MutableStateFlow("")
    val query : StateFlow<String?> = _query.asStateFlow()
    enum class SortField { NAME, CREATED }
    enum class SortOrder { ASC, DESC }
    private val _sortField = MutableStateFlow(SortField.CREATED)
    private val _sortOrder = MutableStateFlow(SortOrder.DESC)
    private val _selectedName = MutableStateFlow<String?>(null)
    val selectedName : StateFlow<String?> = _selectedName
    val sortField: StateFlow<SortField> = _sortField.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()
    // UI-Ausgabe: Views nach Filter, Suche und Sortierung


    val displayedViews: StateFlow<List<ViewLayout>> = combine(
        viewFlowview,
        _filter,
        _query,
        _sortField,
        _sortOrder
    ) { list, filter, query, sortField, sortOrder ->
        // a) Filter nach Name (wenn angegeben)
        val byFilter = filter?.let { f ->
            list.filter { it.name.contains(f, ignoreCase = true) }
        } ?: list
        // b) Suche nach Query im Namen
        val byQuery = if (query.isBlank()) byFilter
        else byFilter.filter { it.name.contains(query, ignoreCase = true) }
        // c) Sortierung
        val sorted = byQuery.sortedWith(
            compareBy<ViewLayout> {
                when (sortField) {
                    SortField.NAME      -> it.name.lowercase()
                    SortField.CREATED   -> it.creationTS
                }
            }.let { cmp -> if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp }
        )
        sorted
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val displayedViewsHome: StateFlow<List<ViewLayout>> = combine(
        viewFlowHome,
        _filter,
        _query,
        _sortField,
        _sortOrder
    ) { list, filter, query, sortField, sortOrder ->
        // a) Filter nach Name (wenn angegeben)
        val byFilter = filter?.let { f ->
            list.filter { it.name.contains(f, ignoreCase = true) }
        } ?: list
        // b) Suche nach Query im Namen
        val byQuery = if (query.isBlank()) byFilter
        else byFilter.filter { it.name.contains(query, ignoreCase = true) }
        // c) Sortierung
        val sorted = byQuery.sortedWith(
            compareBy<ViewLayout> {
                when (sortField) {
                    SortField.NAME      -> it.name.lowercase()
                    SortField.CREATED   -> it.creationTS
                }
            }.let { cmp -> if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp }
        )
        sorted
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )


    // Aktuell ausgewählte View-ID
    private val _selectedViewId = MutableStateFlow<String?>(null)
    val selectedViewId: StateFlow<String?> = _selectedViewId

    // Issues für die ausgewählte View
    val issuesForSelectedView: StateFlow<List<IssueLayout>> = _selectedViewId
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
        // Automatische Auswahl der ersten View

    }
    val selectedViewName: StateFlow<String> = kotlinx.coroutines.flow.combine(
        displayedViewsHome,       // Flow<List<ViewLayout>>
        _selectedViewId       // Flow<String?>
    ) { views, selectedId ->
        selectedId
            ?.let { id -> views.firstOrNull { it.id == id }?.name }
            ?: views.firstOrNull()?.name
                .orEmpty()
    }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = ""
        )

    /** Setze Filter-String */
    fun setFilter(f: String?) { _filter.value = f }

    /** Setze Such-Query */
    fun setQuery(q: String) { _query.value = q }

    /** Toggle oder setze Sortierfeld */
    fun setSortField(field: SortField) {
        _sortField.value = field }
    fun toggleSortOrder() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC
    }

    /** Wechselt die aktuell ausgewählte View */
    fun selectView(viewId: String) {
        _selectedViewId.value = viewId
    }

    /** Erneuert die Views (nennt die API erneut) */
    fun reloadViews() {
        // viewFlow liefert automatisch Updates
    }

    /** Setze Sortierreihenfolge */
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }
    fun setSelectedName(name: String?) {
        _selectedName.value = name

    }

    init {
        viewModelScope.launch {
            displayedViewsHome
                .filter { it.isNotEmpty() }             // warte bis die Liste nicht mehr leer ist
                .first()                                 // nur das erste nicht‑leere Ergebnis
                .let { firstList ->
                    if (_selectedViewId.value == null) {
                        _selectedViewId.value = firstList.first().id
                    }
                }
        }
    }
}