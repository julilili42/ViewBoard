package com.example.viewboard.backend.util

import com.example.viewboard.backend.dataLayout.IssueLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun filterIssuesByLabels(issues: Flow<List<IssueLayout>>, labels: List<String>) : Flow<List<IssueLayout>> {
    return issues.map { issue ->
        issue.filter { it.labels.containsAll(labels) }
            .ifEmpty { emptyList() }
    }
}

fun filterIssuesByAssignments(issues: Flow<List<IssueLayout>>, assignments: List<String>) : Flow<List<IssueLayout>> {
    return issues.map { issue ->
        issue.filter { it.assignments.containsAll(assignments) }
            .ifEmpty { emptyList() }
    }
}

fun filterIssuesByCreators(issues: Flow<List<IssueLayout>>, creators: List<String>) : Flow<List<IssueLayout>> {
    return issues.map { issue ->
        issue.filter { it.creator in creators }
            .ifEmpty { emptyList() }
    }
}