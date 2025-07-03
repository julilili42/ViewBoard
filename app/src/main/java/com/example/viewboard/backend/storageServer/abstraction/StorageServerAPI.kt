package com.example.viewboard.backend.storageServer.abstraction

import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.LabelLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import kotlinx.coroutines.flow.Flow

abstract class StorageServerAPI () {
    public abstract fun init()

    public abstract fun addProject(projectLayout: ProjectLayout, onSuccess: (String) -> Unit = {}, onFailure: (ProjectLayout) -> Unit = {})

    public abstract fun rmProject(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract fun updProject(projectLayout: ProjectLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract fun updProject(id: String, projectLayout: ProjectLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract suspend fun getProject(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : ProjectLayout?

    public abstract fun getProjects() : Flow<List<ProjectLayout>>

    public abstract suspend fun addLabel(projID: String, labelLayout: LabelLayout, onSuccess: (String) -> Unit = {}, onFailure: (LabelLayout) -> Unit = {})

    public abstract suspend fun rmLabel(projID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract suspend fun addLabelToIssue(issueID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract suspend fun rmLabelFromIssue(issueID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract fun updLabel(labelLayout: LabelLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract fun updLabel(id: String, labelLayout: LabelLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract suspend fun getLabel(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : LabelLayout?

    public abstract fun getLabels() : Flow<List<LabelLayout>>

    public abstract fun getLabels(projID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : Flow<List<LabelLayout>>

    public abstract suspend fun addIssue(projID: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit = {}, onFailure: (IssueLayout) -> Unit = {})

    public abstract suspend fun addIssue(projID: String, viewID: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit = {}, onFailure: (IssueLayout) -> Unit = {})

    public abstract suspend fun rmIssue(projID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract suspend fun addIssueToView(viewID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract suspend fun rmIssueFromView(viewID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract fun updIssue(issueLayout: IssueLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract fun updIssue(id: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract suspend fun getIssue(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : IssueLayout?

    public abstract fun getIssues() : Flow<List<IssueLayout>>

    public abstract fun getIssuesFromView(viewID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : Flow<List<IssueLayout>>

    public abstract fun getIssues(projID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : Flow<List<IssueLayout>>

    public abstract suspend fun addView(projID: String, viewLayout: ViewLayout, onSuccess: (String) -> Unit = {}, onFailure: (ViewLayout) -> Unit = {})

    public abstract suspend fun rmView(projID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract fun updView(viewLayout: ViewLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract fun updView(id: String, viewLayout: ViewLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    public abstract suspend fun getView(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : ViewLayout?

    public abstract fun getViews() : Flow<List<ViewLayout>>

    public abstract fun getViews(projID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : Flow<List<ViewLayout>>

    protected lateinit var m_projects: Flow<List<ProjectLayout>>
    protected lateinit var m_labels: Flow<List<LabelLayout>>
    protected lateinit var m_issues: Flow<List<IssueLayout>>
    protected lateinit var m_views: Flow<List<ViewLayout>>
}