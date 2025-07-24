package com.example.viewboard.stateholder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.viewboard.backend.auth.impl.AuthAPI
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.ui.screens.project.ProjectFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {
    enum class SortField { DATE, NAME }
    enum class SortOrder { ASC, DESC }

    private val userId = AuthAPI.getUid() ?: ""
    private val _allProjects = MutableStateFlow<List<ProjectLayout>>(emptyList())
    private val _filter = MutableStateFlow(ProjectFilter.SHARED)
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    private val _sortField = MutableStateFlow(SortField.DATE)
    private val _sortOrder = MutableStateFlow(SortOrder.ASC)
    val sortField: StateFlow<SortField> = _sortField.asStateFlow()
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()
    
    fun setFilter(mode: ProjectFilter) {
        _filter.value = mode
        reload()
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    fun setSortField(field: SortField) {
        _sortField.value = field
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun reload() {
        viewModelScope.launch {
            FirebaseAPI
                .getAllProjects()
                .collectLatest { projects ->
                    _allProjects.value = projects
                }
        }
    }
    
    private val _filteredView = combine(
        _allProjects,
        _query,
        _sortField
    ) { list, q, sortField ->
        val filtered = if (q.isBlank()) {
            list
        } else {
            list.filter { it.name.contains(q, ignoreCase = true) }
        }
        Pair(filtered, sortField)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Pair(emptyList<ProjectLayout>(), SortField.DATE)
    )

    val displayedViewProjects: StateFlow<List<ProjectLayout>> = combine(
        _filteredView,
        _sortOrder
    ) { (list, sortField), sortOrder ->
        list.sortedWith(compareBy<ProjectLayout> {
            when (sortField) {
                SortField.DATE -> it.creationTS // Long timestamp
                SortField.NAME -> it.name.lowercase()
            }
        }.let { cmp -> if (sortOrder == SortOrder.DESC) cmp.reversed() else cmp })
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
    private val _filtered = combine(
        _allProjects,
        _filter,
        _query,
        _sortField
    ) { list, filter, q, sortField ->
        val filtered = when (filter) {
            ProjectFilter.CREATED -> list.filter { it.creator == userId }
            ProjectFilter.SHARED  -> list.filterNot { it.creator == userId }
            ProjectFilter.ALL     -> list
        }
            .let { base -> if (q.isBlank()) base else base.filter { it.name.contains(q, ignoreCase = true) } }

        Pair(filtered, sortField)
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Pair(emptyList(), SortField.DATE)
    )

    val displayedProjects: StateFlow<List<ProjectLayout>> = combine(
        _filtered,
        _sortOrder
    ) { (list, sortField), sortOrder ->
        list.sortedWith(compareBy<ProjectLayout> {
            when (sortField) {
                SortField.DATE -> it.creationTS
                SortField.NAME -> it.name.lowercase()
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

