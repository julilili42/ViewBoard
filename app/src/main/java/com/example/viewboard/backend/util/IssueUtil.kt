package com.example.viewboard.backend.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Get all issues filtered by a set of labels
 *
 * @param issues the issues to be filtered
 * @param labels the set of labels to be used for filtering
 *
 * @return the issues when they have been successfully filtered
 */
fun filterIssuesByLabels(issues: Flow<List<IssueLayout>>, labels: List<String>) : Flow<List<IssueLayout>> {
    return issues.map { issue ->
        issue.filter { it.labels.containsAll(labels) }
            .ifEmpty { emptyList() }
    }
}


/**
 * Get all issues filtered by a set of assignments
 *
 * @param issues the issues to be filtered
 * @param assignments the set of assignments to be used for filtering
 *
 * @return the issues when they have been successfully filtered
 */
fun filterIssuesByAssignments(issues: Flow<List<IssueLayout>>, assignments: List<String>) : Flow<List<IssueLayout>> {
    return issues.map { issue ->
        issue.filter { it.assignments.containsAll(assignments) }
            .ifEmpty { emptyList() }
    }
}

/**
 * Get all issues filtered by a set of creators
 *
 * @param issues the issues to be filtered
 * @param creators the set of creators to be used for filtering
 *
 * @return the issues when they have been successfully filtered
 */
fun filterIssuesByCreators(issues: Flow<List<IssueLayout>>, creators: List<String>) : Flow<List<IssueLayout>> {
    return issues.map { issue ->
        issue.filter { it.creator in creators }
            .ifEmpty { emptyList() }
    }
}

/**
 * Get all issues filtered by a set of states
 *
 * @param issues the issues to be filtered
 * @param states the set of states to be used for filtering
 *
 * @return the issues when they have been successfully filtered
 */
fun filterIssuesByStates(issues: Flow<List<IssueLayout>>, states: List<IssueState>) : Flow<List<IssueLayout>> {
    return issues.map { issue ->
        issue.filter { it.state in states }
            .ifEmpty { emptyList() }
    }
}