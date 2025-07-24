package com.example.viewboard.dataclass

import com.example.viewboard.stateholder.IssueViewModel
import com.example.viewboard.stateholder.ProjectViewModel

public data class SortOptionsProject(
    val label: String,
    val field: ProjectViewModel.SortField
)
public data class SortOptionsIssues(
    val label: String,
    val field: IssueViewModel.SortField
)
