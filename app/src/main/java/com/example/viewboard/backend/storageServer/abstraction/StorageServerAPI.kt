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

    public abstract fun getProjects() : Flow<List<ProjectLayout>>
    
    public abstract fun addLabel(labelLayout: LabelLayout)

    public abstract fun rmLabel(labelLayout: LabelLayout)

    public abstract fun rmLabel(id: String)

    public abstract fun updLabel(labelLayout: LabelLayout)

    public abstract fun updLabel(id: String, labelLayout: LabelLayout)

    public abstract fun getLabels() : Flow<List<LabelLayout>>

    public abstract fun addIssue(issueLayout: IssueLayout)

    public abstract fun rmIssue(issueLayout: IssueLayout)

    public abstract fun rmIssue(id: String)

    public abstract fun updIssue(issueLayout: IssueLayout)

    public abstract fun updIssue(id: String, issueLayout: IssueLayout)

    public abstract fun getIssues() : Flow<List<IssueLayout>>

    public abstract fun addView(viewLayout: ViewLayout)

    public abstract fun rmView(viewLayout: ViewLayout)

    public abstract fun rmView(id: String)

    public abstract fun updView(viewLayout: ViewLayout)

    public abstract fun updView(id: String, viewLayout: ViewLayout)

    public abstract fun getViews() : Flow<List<ViewLayout>>

    protected lateinit var m_projects: Flow<List<ProjectLayout>>
    protected lateinit var m_labels: Flow<List<LabelLayout>>
    protected lateinit var m_issues: Flow<List<IssueLayout>>
    protected lateinit var m_views: Flow<List<ViewLayout>>

    // TODO could the get functions provide a read only ref ?
}