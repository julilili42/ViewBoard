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
/**
 * ViewModel für die Projekt‑Ansicht.
 * Hält alle Projekte, Filter‑ und Such‑States als StateFlows,
 * kombiniert sie zu einer angezeigten Liste und lädt bei Bedarf neu.
 */
class ProjectViewModel : ViewModel() {


    // Aktuelle User‑ID
    private val myId = AuthAPI.getUid() ?: ""

    // Alle geladenen Projekte
    private val _allProjects = MutableStateFlow<List<ProjectLayout>>(emptyList())

    // Filter: CREATED/SHARED/ALL
    private val _filter = MutableStateFlow(ProjectFilter.SHARED)

    // Such‑Query
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    // Sortierkriterien
    enum class SortField { DATE, NAME }
    enum class SortOrder { ASC, DESC }

    private val _sortField = MutableStateFlow(SortField.DATE)
    private val _sortOrder = MutableStateFlow(SortOrder.ASC)

    val sortField: StateFlow<SortField> = _sortField.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _filtered = combine(
        _allProjects,
        _filter,
        _query,
        _sortField
    ) { list, filter, q, sortField ->
        // Filter nach Typ
        val filtered = when (filter) {
            ProjectFilter.CREATED -> list.filter { it.creator == myId }
            ProjectFilter.SHARED  -> list.filterNot { it.creator == myId }
            ProjectFilter.ALL     -> list
        }
            // Suche anwenden
            .let { base -> if (q.isBlank()) base else base.filter { it.name.contains(q, ignoreCase = true) } }

        Pair(filtered, sortField)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Pair(emptyList(), SortField.DATE)
    )

    // Endgültige Auslieferung mit SortOrder

    val displayedProjects: StateFlow<List<ProjectLayout>> = combine(
        _filtered,
        _sortOrder
    ) { (list, sortField), sortOrder ->
        list.sortedWith(compareBy<ProjectLayout> {
            when (sortField) {
                SortField.DATE -> it.creationTS // Long timestamp
                SortField.NAME       -> it.name.lowercase()
            }
        }.let { cmp -> if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp })
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    /** Setze den Projekt‑Filter */
    fun setFilter(mode: ProjectFilter) {
        _filter.value = mode
        reload()
    }

    /** Setze die Such‑Query */
    fun setQuery(q: String) {
        _query.value = q
    }

    /** Setze Sortierfeld (Zeit oder Name) */
    fun setSortField(field: SortField) {
        _sortField.value = field
    }

    /** Setze Sortierreihenfolge */
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    /** Lade Projekte entsprechend dem Filter */
    fun reload() {
        viewModelScope.launch {
            FirebaseAPI
                .getAllProjects()                // immer die gesamte Liste holen
                .collectLatest { projects ->
                    _allProjects.value = projects
                }
        }
    }
    fun toggleSort(field: SortField) {
        if (_sortField.value == field) {
            // wechsel zwischen ASC / DESC
            _sortOrder.value = if (_sortOrder.value == SortOrder.ASC) SortOrder.DESC
            else SortOrder.ASC
        } else {
            // neues Feld: immer ASC starten
            _sortField.value = field
            _sortOrder.value = SortOrder.ASC
        }
    }

    private val _filteredView = combine(
        _allProjects,   // alle Projekte
        _query,         // Such-String
        _sortField      // Sortierkriterium
    ) { list, q, sortField ->
        // Suche anwenden (kein Creator‑Filter mehr)
        val filtered = if (q.isBlank()) {
            list
        } else {
            list.filter { it.name.contains(q, ignoreCase = true) }
        }

        // Rückgabe: gefilterte Liste plus Sortierfeld
        Pair(filtered, sortField)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Pair(emptyList<ProjectLayout>(), SortField.DATE)
    )

    val displayedviewProjects: StateFlow<List<ProjectLayout>> = combine(
        _filteredView,
        _sortOrder
    ) { (list, sortField), sortOrder ->
        list.sortedWith(compareBy<ProjectLayout> {
            when (sortField) {
                SortField.DATE -> it.creationTS // Long timestamp
                SortField.NAME       -> it.name.lowercase()
            }
        }.let { cmp -> if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp })
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )


    init {
        reload()
    }
}

