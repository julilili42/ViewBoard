package com.example.viewboard.ui.issue


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class ViewsViewModel : ViewModel() {

    companion object {
        private const val TAG = "ViewsViewModel"
    }

    // Alle geladenen Views
    private val _allViews = MutableStateFlow<List<ViewLayout>>(emptyList())
    val allViews: StateFlow<List<ViewLayout>> = _allViews.asStateFlow()

    // Such‑Query
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // Sortierkriterien: nach Erstellungsdatum oder Name
    enum class SortField { DATE, NAME }
    enum class SortOrder { ASC, DESC }

    private val _sortField = MutableStateFlow(SortField.DATE)
    private val _sortOrder = MutableStateFlow(SortOrder.ASC)
    val sortField: StateFlow<SortField> = _sortField.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // Kombiniere Suche und Sort‑Feld
    private val _filtered = combine(
        _allViews,
        _query,
        _sortField
    ) { list, q, sortField ->
        // Log ob wir filtern
        Log.d(TAG, "Applying filter – query='$q', sortField=$sortField")
        val filtered = if (q.isBlank()) list
        else list.filter { it.name.contains(q, ignoreCase = true) }
        Pair(filtered, sortField)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Pair(emptyList(), SortField.DATE)
    )

    // Endgültige Auslieferung mit SortOrder
    val displayedViews = combine(
        _filtered,
        _sortOrder
    ) { (list, sortField), sortOrder ->
        Log.d(TAG, "Sorting – field=$sortField, order=$sortOrder, inputSize=${list.size}")
        list.sortedWith(
            compareBy<ViewLayout> {
                when (sortField) {
                    SortField.DATE -> it.creationTS
                    SortField.NAME -> it.name.lowercase()
                }
            }.let { cmp -> if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    /** Setze die Such‑Query */
    fun setQuery(q: String) {
        Log.d(TAG, "setQuery: '$q'")
        _query.value = q
    }

    /** Toggle Sort‑Feld (ASC↔DESC oder neues Feld) */
    fun toggleSort(field: SortField) {
        val newOrder = if (_sortField.value == field) {
            if (_sortOrder.value == SortOrder.ASC) SortOrder.DESC else SortOrder.ASC
        } else {
            SortOrder.ASC
        }
        Log.d(TAG, "toggleSort: field=$field -> order=$newOrder")
        _sortField.value = field
        _sortOrder.value = newOrder
    }

    /** Lade nur die Views dieses Users */
    fun reload() {
        viewModelScope.launch {
            val uid = AuthAPI.getUid()
            Log.d(TAG, "Starting reload() for user=$uid")
            if (uid == null) {
                Log.w(TAG, "No user ID, skipping load")
                _allViews.value = emptyList()
                return@launch
            }
            try {
                val views = FirebaseAPI.getViewsFromUser(uid)
                Log.d(TAG, "Loaded ${views.size} views from remote for user=$uid")
                _allViews.value = views
            } catch (e: Exception) {
                Log.e(TAG, "Error loading views for user=$uid", e)
            }
        }
    }
    private val _issues = MutableStateFlow<List<IssueLayout>>(emptyList())
    val issues: StateFlow<List<IssueLayout>> = _issues.asStateFlow()

    fun loadIssuesFromView(viewID: String) {
        viewModelScope.launch {
            FirebaseAPI.getIssuesFromView(viewID)
                .collectLatest { _issues.value = it }
        }
    }
    // 1) StateFlows für geladene Views
    private val _viewLayouts = MutableStateFlow<List<ViewLayout>>(emptyList())
    val viewLayouts: StateFlow<List<ViewLayout>> = _viewLayouts.asStateFlow()

    /**
     * Lädt alle ViewLayout-Objekte für die gegebenen viewIDs.
     */
    fun loadViewsByIds(ids: List<String>) {
        viewModelScope.launch {
            val loaded = ids.mapNotNull { id ->
                // FirebaseAPI.getView ist suspend und liefert ViewLayout?
                FirebaseAPI.getView(
                    id,
                    onSuccess = { /* ignorieren */ },
                    onFailure = { /* hier könntest du loggen */ }
                )
            }
            _viewLayouts.value = loaded
        }
    }

    init {
        reload()
    }
}
