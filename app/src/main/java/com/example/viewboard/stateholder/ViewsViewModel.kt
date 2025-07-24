package com.example.viewboard.stateholder

import kotlinx.coroutines.flow.map
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.dataLayout.ViewLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ViewsViewModel : ViewModel() {
    enum class SortField { NAME, CREATED }
    enum class SortOrder { ASC, DESC }
    
    private val userId: String = AuthAPI.getUid() ?: ""

    private val _filter = MutableStateFlow<String?>(null)
    private val _query = MutableStateFlow("")
    val query : StateFlow<String?> = _query.asStateFlow()

    private val _sortField = MutableStateFlow(SortField.CREATED)
    private val _sortOrder = MutableStateFlow(SortOrder.DESC)

    val sortField: StateFlow<SortField> = _sortField.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()
    
    private val _selectedViewId = MutableStateFlow<String?>(null)
    val selectedViewId: StateFlow<String?> = _selectedViewId
    
    fun setQuery(q: String) { _query.value = q }
    
    fun setSortField(field: SortField) {
        _sortField.value = field }
    
    fun selectView(viewId: String) {
        _selectedViewId.value = viewId
    }
    
    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    val viewFlowForView: Flow<List<ViewLayout>> = FirebaseAPI.getAllViews()
        .map { views -> views.filter { it.creator == userId } }

    val viewFlowForHome: Flow<List<ViewLayout>> = FirebaseAPI.getAllViews()
        .map { views ->
            views.filter { view ->
                view.creator == userId && view.issues.isNotEmpty()  // nur Views mit mindestens einem Issue
            }
        }
    val displayedViews: StateFlow<List<ViewLayout>> = combine(
        viewFlowForView,
        _filter,
        _query,
        _sortField,
        _sortOrder
    ) { list, filter, query, sortField, sortOrder ->

        val byFilter = filter?.let { f ->
            list.filter { it.name.contains(f, ignoreCase = true) }
        } ?: list

        val byQuery = if (query.isBlank()) byFilter
        else byFilter.filter { it.name.contains(query, ignoreCase = true) }

        val sorted = byQuery.sortedWith(
            compareBy<ViewLayout> {
                when (sortField) {
                    SortField.NAME -> it.name.lowercase()
                    SortField.CREATED -> it.creationTS
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
        viewFlowForHome,
        _filter,
        _query,
        _sortField,
        _sortOrder
    ) { list, filter, query, sortField, sortOrder ->

        val byFilter = filter?.let { f ->
            list.filter { it.name.contains(f, ignoreCase = true) }
        } ?: list

        val byQuery = if (query.isBlank()) byFilter
        else byFilter.filter { it.name.contains(query, ignoreCase = true) }

        val sorted = byQuery.sortedWith(
            compareBy<ViewLayout> {
                when (sortField) {
                    SortField.NAME -> it.name.lowercase()
                    SortField.CREATED -> it.creationTS
                }
            }.let { cmp -> if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp }
        )
        sorted
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val selectedViewName: StateFlow<String> = kotlinx.coroutines.flow.combine(
        displayedViewsHome,
        _selectedViewId
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
    
    init {
        viewModelScope.launch {
            displayedViewsHome
                .filter { it.isNotEmpty() }        
                .first()                              
                .let { firstList ->
                    if (_selectedViewId.value == null) {
                        _selectedViewId.value = firstList.first().id
                    }
                }
        }
    }
}