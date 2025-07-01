package com.example.viewboard.backend.storageServer.abstraction

import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.LabelLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import kotlinx.coroutines.flow.Flow

abstract class StorageServerAPI () {
    public abstract fun addProject(projectLayout: ProjectLayout)

    public abstract fun rmProject(projectLayout: ProjectLayout)

    public abstract fun rmProject(id: String)

    public abstract fun updProject(projectLayout: ProjectLayout)

    public abstract fun updProject(id: String, projectLayout: ProjectLayout)

    public abstract suspend fun getProject(id: String) : ProjectLayout?

    public abstract fun getProjects() : Flow<List<ProjectLayout>>

    public abstract suspend fun addLabel(projID: String, labelLayout: LabelLayout)

    public abstract suspend fun rmLabel(projID: String, labelLayout: LabelLayout)

    public abstract suspend fun rmLabel(projID: String, id: String)

    public abstract fun updLabel(labelLayout: LabelLayout)

    public abstract fun updLabel(id: String, labelLayout: LabelLayout)

    public abstract suspend fun getLabel(id: String) : LabelLayout?

    public abstract fun getLabels() : Flow<List<LabelLayout>>

    public abstract fun getLabels(projID: String) : Flow<List<LabelLayout>>

    public abstract suspend fun addIssue(projID: String, issueLayout: IssueLayout)

    public abstract suspend fun rmIssue(projID: String, issueLayout: IssueLayout)

    public abstract suspend fun rmIssue(projID: String, id: String)

    public abstract fun updIssue(issueLayout: IssueLayout)

    public abstract fun updIssue(id: String, issueLayout: IssueLayout)

    public abstract suspend fun getIssue(id: String) : IssueLayout?

    public abstract fun getIssues() : Flow<List<IssueLayout>>

    public abstract fun getIssues(projID: String) : Flow<List<IssueLayout>>

    public abstract suspend fun addView(projID: String, viewLayout: ViewLayout)

    public abstract suspend fun rmView(projID: String, viewLayout: ViewLayout)

    public abstract suspend fun rmView(projID: String, id: String)

    public abstract fun updView(viewLayout: ViewLayout)

    public abstract fun updView(id: String, viewLayout: ViewLayout)

    public abstract suspend fun getView(id: String) : ViewLayout?

    public abstract fun getViews() : Flow<List<ViewLayout>>

    public abstract fun getViews(projID: String) : Flow<List<ViewLayout>>

    protected lateinit var m_projects: Flow<List<ProjectLayout>>
    protected lateinit var m_labels: Flow<List<LabelLayout>>
    protected lateinit var m_issues: Flow<List<IssueLayout>>
    protected lateinit var m_views: Flow<List<ViewLayout>>
}