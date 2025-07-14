package com.example.viewboard.backend.storageServer.abstraction

import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.LabelLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import kotlinx.coroutines.flow.Flow

abstract class StorageServerAPI () {
    /**
     * Initialize the StorageServerAPI
     */
    public abstract fun init()

    /**
     * Add a project
     *
     * @param projectLayout the project in an initial state
     * @param onSuccess success callback, when the project has been successfully added
     * @param onFailure failure callback, if the project has not been added
     */
    public abstract fun addProject(projectLayout: ProjectLayout, onSuccess: (String) -> Unit = {}, onFailure: (ProjectLayout) -> Unit = {})

    /**
     * Remove a project
     *
     * @param id the id of the project
     * @param onSuccess success callback, when the project has been successfully removed
     * @param onFailure failure callback, if the project has not been removed
     */
    public abstract fun rmProject(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Update a project
     *
     * @param projectLayout the updated project state
     * @param onSuccess success callback, when the project has been successfully updated
     * @param onFailure failure callback, if the project has not been updated
     */
    public abstract fun updProject(projectLayout: ProjectLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Update a project
     *
     * @param id the id of the project
     * @param projectLayout the new project state
     * @param onSuccess success callback, when the project has been successfully updated
     * @param onFailure failure callback, if the project has not been updated
     */
    public abstract fun updProject(id: String, projectLayout: ProjectLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Get a project
     *
     * @param id the id of the project
     * @param onSuccess success callback, when the project has been successfully retrieved
     * @param onFailure failure callback, if the project has not been retrieved
     *
     * @return the project if it has been successfully retrieved, otherwise null
     */
    public abstract suspend fun getProject(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : ProjectLayout?

    /**
     * Get all projects
     *
     * @return the projects
     */
    public abstract fun getAllProjects() : Flow<List<ProjectLayout>>

    /**
     * Get all projects from user
     *
     * @param userID the id of the user
     *
     * @return the projects when they have been successfully retrieved
     */
    public abstract fun getProjectsFromUser(userID: String?) : Flow<List<ProjectLayout>>

    /**
     * Add a label
     *
     * @param projID the id of the project
     * @param labelLayout the label in an initial state
     * @param onSuccess success callback, when the label has been successfully added
     * @param onFailure failure callback, if the label has not been added
     */
    public abstract suspend fun addLabel(projID: String, labelLayout: LabelLayout, onSuccess: (String) -> Unit = {}, onFailure: (LabelLayout) -> Unit = {})

    /**
     * Remove a label
     *
     * @param projID the id of the project
     * @param id the id of the label
     * @param onSuccess success callback, when the label has been successfully removed
     * @param onFailure failure callback, if the label has not been removed
     */
    public abstract suspend fun rmLabel(projID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Add a label to a issue
     *
     * @param issueID the id of an issue
     * @param id the id of the label
     * @param onSuccess success callback, when the label has been successfully added
     * @param onFailure failure callback, if the label has not been added
     */
    public abstract suspend fun addLabelToIssue(issueID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Remove a label from issue
     *
     * @param issueID the id of an issue
     * @param id the id of the label
     * @param onSuccess success callback, when the label has been successfully removed
     * @param onFailure failure callback, if the label has not been removed
     */
    public abstract suspend fun rmLabelFromIssue(issueID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Update a label
     *
     * @param labelLayout the updated label state
     * @param onSuccess success callback, when the label has been successfully updated
     * @param onFailure failure callback, if the label has not been updated
     */
    public abstract fun updLabel(labelLayout: LabelLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Update a label
     *
     * @param id the id of the label
     * @param labelLayout the new label state
     * @param onSuccess success callback, when the label has been successfully updated
     * @param onFailure failure callback, if the label has not been updated
     */
    public abstract fun updLabel(id: String, labelLayout: LabelLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Get a label
     *
     * @param id the id of the label
     * @param onSuccess success callback, when the label has been successfully retrieved
     * @param onFailure failure callback, if the label has not been retrieved
     *
     * @return the label if it has been successfully retrieved, otherwise null
     */
    public abstract suspend fun getLabel(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : LabelLayout?

    /**
     * Get all labels
     *
     * @return the labels
     */
    public abstract fun getAllLabels() : Flow<List<LabelLayout>>

    /**
     * Get all labels
     *
     * @param projID the id of the project
     * @param onSuccess success callback, when the labels have been successfully retrieved
     * @param onFailure failure callback, if the labels have not been retrieved
     *
     * @return the labels when they have been successfully retrieved
     */
    public abstract fun getLabelsFromProject(projID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : Flow<List<LabelLayout>>

    /**
     * Add a issue
     *
     * @param projID the id of the project
     * @param issueLayout an issue in an initial state
     * @param onSuccess success callback, when the issue has been successfully added
     * @param onFailure failure callback, if the issue has not been added
     */
    public abstract suspend fun addIssue(projID: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit = {}, onFailure: (IssueLayout) -> Unit = {})

    /**
     * Add an issue
     *
     * @param projID the id of the project
     * @param viewID the id of the view
     * @param issueLayout an issue in an initial state
     * @param onSuccess success callback, when the issue has been successfully added
     * @param onFailure failure callback, if the issue has not been added
     */
    public abstract suspend fun addIssue(projID: String, viewID: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit = {}, onFailure: (IssueLayout) -> Unit = {})

    /**
     * Remove an issue
     *
     * @param projID the id of the project
     * @param id the id of the issue
     * @param onSuccess success callback, when the issue has been successfully removed
     * @param onFailure failure callback, if the issue has not been removed
     */
    public abstract suspend fun rmIssue(projID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Add an issue to a view
     *
     * @param viewID the id of the view
     * @param id the id of an issue
     * @param onSuccess success callback, when the issue has been successfully added
     * @param onFailure failure callback, if the issue has not been added
     */
    public abstract suspend fun addIssueToView(viewID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Remove an issue from view
     *
     * @param viewID the id of the view
     * @param id the id of an issue
     * @param onSuccess success callback, when the issue has been successfully removed
     * @param onFailure failure callback, if the issue has not been removed
     */
    public abstract suspend fun rmIssueFromView(viewID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Update an issue
     *
     * @param issueLayout the updated issue state
     * @param onSuccess success callback, when the issue has been successfully updated
     * @param onFailure failure callback, if the issue has not been updated
     */
    public abstract fun updIssue(issueLayout: IssueLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Update an issue
     *
     * @param id the id of the issue
     * @param issueLayout the new issue state
     * @param onSuccess success callback, when the issue has been successfully updated
     * @param onFailure failure callback, if the issue has not been updated
     */
    public abstract fun updIssue(id: String, issueLayout: IssueLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Get an issue
     *
     * @param id the id of the issue
     * @param onSuccess success callback, when the label has been successfully retrieved
     * @param onFailure failure callback, if the label has not been retrieved
     *
     * @return the issue if it has been successfully retrieved, otherwise null
     */
    public abstract suspend fun getIssue(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : IssueLayout?

    /**
     * Get all issues
     *
     * @return the issues
     */
    public abstract fun getAllIssues() : Flow<List<IssueLayout>>

    /**
     * Get all issues from view
     *
     * @param viewID the id of the view
     * @param onSuccess success callback, when the issues have been successfully retrieved
     * @param onFailure failure callback, if the issues have not been retrieved
     *
     * @return the issues when they have been successfully retrieved
     */
    public abstract fun getIssuesFromView(viewID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : Flow<List<IssueLayout>>

    /**
     * Get all issues
     *
     * @param projID the id of the project
     * @param onSuccess success callback, when the issues have been successfully retrieved
     * @param onFailure failure callback, if the issues have not been retrieved
     *
     * @return the issues when they have been successfully retrieved
     */
    public abstract fun getIssuesFromProject(projID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : Flow<List<IssueLayout>>

    /**
     * Get all issues from assignment
     *
     * @param userID the id of the assignment
     *
     * @return the issues when they have been successfully retrieved
     */
    public abstract fun getIssuesFromAssignment(userID: String?) : Flow<List<IssueLayout>>

    /**
     * Get all issues from assignment
     *
     * @param userID the id of the assignment
     * @param projID the id of the project
     *
     * @return the issues when they have been successfully retrieved
     */
    public abstract fun getIssuesFromAssignment(userID: String?, projID: String) : Flow<List<IssueLayout>>

    /**
     * Get all issues from creator
     *
     * @param userID the id of the creator
     *
     * @return the issues when they have been successfully retrieved
     */
    public abstract fun getIssuesFromCreator(userID: String?) : Flow<List<IssueLayout>>

    /**
     * Get all issues from creator for a project
     *
     * @param userID the id of the creator
     * @param projID the id of the project
     *
     * @return the issues when they have been successfully retrieved
     */
    public abstract fun getIssuesFromCreator(userID: String?, projID: String) : Flow<List<IssueLayout>>

    /**
     * Get all issues from user
     *
     * @param userID the id of the user
     *
     * @return the issues when they have been successfully retrieved
     */
    public abstract fun getIssuesFromUser(userID: String?) : Flow<List<IssueLayout>>

    /**
     * Get all issues from user for a project
     *
     * @param userID the id of the user
     * @param projID the id of the project
     *
     * @return the issues when they have been successfully retrieved
     */
    public abstract fun getIssuesFromUser(userID: String?, projID: String) : Flow<List<IssueLayout>>

    /**
     * Add a view
     *
     * @param projID the id of the project
     * @param viewLayout a view in an initial state
     * @param onSuccess success callback, when the view has been successfully added
     * @param onFailure failure callback, if the view has not been added
     */
    public abstract suspend fun addView(projID: String, viewLayout: ViewLayout, onSuccess: (String) -> Unit = {}, onFailure: (ViewLayout) -> Unit = {})

    /**
     * Remove a view
     *
     * @param projID the id of the project
     * @param id the id of the view
     * @param onSuccess success callback, when the view has been successfully removed
     * @param onFailure failure callback, if the view has not been removed
     */
    public abstract suspend fun rmView(projID: String, id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Update a view
     *
     * @param viewLayout the updated view state
     * @param onSuccess success callback, when the view has been successfully updated
     * @param onFailure failure callback, if the view has not been updated
     */
    public abstract fun updView(viewLayout: ViewLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Update a view
     *
     * @param id the id of the view
     * @param viewLayout the new view state
     * @param onSuccess success callback, when the view has been successfully updated
     * @param onFailure failure callback, if the view has not been updated
     */
    public abstract fun updView(id: String, viewLayout: ViewLayout, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {})

    /**
     * Get a view
     *
     * @param id the id of the view
     * @param onSuccess success callback, when the view has been successfully retrieved
     * @param onFailure failure callback, if the view has not been retrieved
     *
     * @return the view if it has been successfully retrieved, otherwise null
     */
    public abstract suspend fun getView(id: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : ViewLayout?

    /**
     * Get all views
     *
     * @return the views
     */
    public abstract fun getAllViews() : Flow<List<ViewLayout>>

    /**
     * Get all views from
     *
     * @param projID the id of the project
     * @param onSuccess success callback, when the views have been successfully retrieved
     * @param onFailure failure callback, if the views have not been retrieved
     *
     * @return the views when they have been successfully retrieved
     */
    public abstract fun getViewsFromProject(projID: String, onSuccess: (String) -> Unit = {}, onFailure: (String) -> Unit = {}) : Flow<List<ViewLayout>>

    protected lateinit var m_projects: Flow<List<ProjectLayout>>
    protected lateinit var m_labels: Flow<List<LabelLayout>>
    protected lateinit var m_issues: Flow<List<IssueLayout>>
    protected lateinit var m_views: Flow<List<ViewLayout>>
}