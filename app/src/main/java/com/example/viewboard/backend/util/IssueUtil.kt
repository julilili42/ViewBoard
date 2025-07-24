package com.example.viewboard.backend.util

import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.IssueState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Get all issues filtered by a set of users
 *
 * @param issues the issues to be filtered
 * @param users a set of users to be used for filtering
 *
 * @return the issues when they have been successfully filtered
 */
fun filterIssuesByUsers(issues: Flow<List<IssueLayout>>, users: List<String>) : Flow<List<IssueLayout>> {
    return issues.map { issue ->
        issue.filter { it.users.containsAll(users) }
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