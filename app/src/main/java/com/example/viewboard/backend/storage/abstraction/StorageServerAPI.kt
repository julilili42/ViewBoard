package com.example.viewboard.backend.storage.abstraction

import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.dataLayout.ProjectLayout
import com.example.viewboard.backend.dataLayout.UserLayout
import com.example.viewboard.backend.dataLayout.ViewLayout
import kotlinx.coroutines.flow.Flow

abstract class StorageServerAPI() {
    /**
     * Initialize the StorageServerAPI
     */
    abstract fun init()

    /**
     * Add a project
     *
     * @param projectLayout the project in an initial state
     * @param onSuccess success callback, when the project has been successfully added
     * @param onFailure failure callback, if the project has not been added
     */
    abstract fun addProject(
        projectLayout: ProjectLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (ProjectLayout) -> Unit = {}
    )

    /**
     * Remove a project
     *
     * @param id the id of the project
     * @param onSuccess success callback, when the project has been successfully removed
     * @param onFailure failure callback, if the project has not been removed
     */
    abstract fun rmProject(
        id: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Update a project
     *
     * @param projectLayout the updated project state
     * @param onSuccess success callback, when the project has been successfully updated
     * @param onFailure failure callback, if the project has not been updated
     */
    abstract fun updProject(
        projectLayout: ProjectLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Update a project
     *
     * @param id the id of the project
     * @param projectLayout the new project state
     * @param onSuccess success callback, when the project has been successfully updated
     * @param onFailure failure callback, if the project has not been updated
     */
    abstract fun updProject(
        id: String,
        projectLayout: ProjectLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Get a project
     *
     * @param id the id of the project
     * @param onSuccess success callback, when the project has been successfully retrieved
     * @param onFailure failure callback, if the project has not been retrieved
     *
     * @return the project if it has been successfully retrieved, otherwise null
     */
    abstract suspend fun getProject(
        id: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ): ProjectLayout?

    /**
     * Get all projects
     *
     * @return the projects
     */
    abstract fun getAllProjects(): Flow<List<ProjectLayout>>

    /**
     * Get all projects from user
     *
     * @param userID the id of the user
     *
     * @return the projects when they have been successfully retrieved
     */
    abstract fun getProjectsFromUser(userID: String?): Flow<List<ProjectLayout>>

    /**
     * Add a issue
     *
     * @param projID the id of the project
     * @param issueLayout an issue in an initial state
     * @param onSuccess success callback, when the issue has been successfully added
     * @param onFailure failure callback, if the issue has not been added
     */
    abstract suspend fun addIssue(
        projID: String,
        issueLayout: IssueLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (IssueLayout) -> Unit = {}
    )

    /**
     * Add an issue
     *
     * @param projID the id of the project
     * @param viewID the id of the view
     * @param issueLayout an issue in an initial state
     * @param onSuccess success callback, when the issue has been successfully added
     * @param onFailure failure callback, if the issue has not been added
     */
    abstract suspend fun addIssue(
        projID: String,
        viewID: String,
        issueLayout: IssueLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (IssueLayout) -> Unit = {}
    )

    /**
     * Remove an issue
     *
     * @param projID the id of the project
     * @param id the id of the issue
     * @param onSuccess success callback, when the issue has been successfully removed
     * @param onFailure failure callback, if the issue has not been removed
     */
    abstract suspend fun rmIssue(
        projID: String,
        id: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Add an issue to a view
     *
     * @param viewID the id of the view
     * @param id the id of an issue
     * @param onSuccess success callback, when the issue has been successfully added
     * @param onFailure failure callback, if the issue has not been added
     */
    abstract suspend fun addIssueToView(
        viewID: String,
        id: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Remove an issue from view
     *
     * @param viewID the id of the view
     * @param id the id of an issue
     * @param onSuccess success callback, when the issue has been successfully removed
     * @param onFailure failure callback, if the issue has not been removed
     */
    abstract suspend fun rmIssueFromView(
        viewID: String,
        id: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Update an issue
     *
     * @param issueLayout the updated issue state
     * @param onSuccess success callback, when the issue has been successfully updated
     * @param onFailure failure callback, if the issue has not been updated
     */
    abstract fun updIssue(
        issueLayout: IssueLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Update an issue
     *
     * @param id the id of the issue
     * @param issueLayout the new issue state
     * @param onSuccess success callback, when the issue has been successfully updated
     * @param onFailure failure callback, if the issue has not been updated
     */
    abstract fun updIssue(
        id: String,
        issueLayout: IssueLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Get an issue
     *
     * @param id the id of the issue
     * @param onSuccess success callback, when the label has been successfully retrieved
     * @param onFailure failure callback, if the label has not been retrieved
     *
     * @return the issue if it has been successfully retrieved, otherwise null
     */
    abstract suspend fun getIssue(
        id: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ): IssueLayout?

    /**
     * Get all issues
     *
     * @return the issues
     */
    abstract fun getAllIssues(): Flow<List<IssueLayout>>

    /**
     * Get all issues from view
     *
     * @param viewID the id of the view
     * @param onSuccess success callback, when the issues have been successfully retrieved
     * @param onFailure failure callback, if the issues have not been retrieved
     *
     * @return the issues when they have been successfully retrieved
     */
    abstract fun getIssuesFromView(
        viewID: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ): Flow<List<IssueLayout>>

    /**
     * Get all issues
     *
     * @param projID the id of the project
     * @param onSuccess success callback, when the issues have been successfully retrieved
     * @param onFailure failure callback, if the issues have not been retrieved
     *
     * @return the issues when they have been successfully retrieved
     */
    abstract fun getIssuesFromProject(
        projID: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ): Flow<List<IssueLayout>>

    /**
     * Get all issues from assignment
     *
     * @param userID the id of the assignment
     *
     * @return the issues when they have been successfully retrieved
     */
    abstract fun getIssuesFromAssignment(userID: String?): Flow<List<IssueLayout>>

    /**
     * Get all issues from assignment
     *
     * @param userID the id of the assignment
     * @param projID the id of the project
     *
     * @return the issues when they have been successfully retrieved
     */
    abstract fun getIssuesFromAssignment(userID: String?, projID: String): Flow<List<IssueLayout>>

    /**
     * Get all issues from creator
     *
     * @param userID the id of the creator
     *
     * @return the issues when they have been successfully retrieved
     */
    abstract fun getIssuesFromCreator(userID: String?): Flow<List<IssueLayout>>

    /**
     * Get all issues from creator for a project
     *
     * @param userID the id of the creator
     * @param projID the id of the project
     *
     * @return the issues when they have been successfully retrieved
     */
    abstract fun getIssuesFromCreator(userID: String?, projID: String): Flow<List<IssueLayout>>

    /**
     * Get all issues from user
     *
     * @param userID the id of the user
     *
     * @return the issues when they have been successfully retrieved
     */
    abstract fun getIssuesFromUser(userID: String?): Flow<List<IssueLayout>>

    /**
     * Get all issues from user for a project
     *
     * @param userID the id of the user
     * @param projID the id of the project
     *
     * @return the issues when they have been successfully retrieved
     */
    abstract fun getIssuesFromUser(userID: String?, projID: String): Flow<List<IssueLayout>>

    /**
     * Add a view
     *
     * @param userID the id of the user
     * @param viewLayout a view in an initial state
     * @param onSuccess success callback, when the view has been successfully added
     * @param onFailure failure callback, if the view has not been added
     */
    abstract suspend fun addView(
        userID: String?,
        viewLayout: ViewLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (ViewLayout) -> Unit = {}
    )

    /**
     * Remove a view
     *
     * @param userID the id of the user
     * @param id the id of the view
     * @param onSuccess success callback, when the view has been successfully removed
     * @param onFailure failure callback, if the view has not been removed
     */
    abstract suspend fun rmView(
        userID: String?,
        id: String,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Update a view
     *
     * @param viewLayout the updated view state
     * @param onSuccess success callback, when the view has been successfully updated
     * @param onFailure failure callback, if the view has not been updated
     */
    abstract fun updView(
        viewLayout: ViewLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Update a view
     *
     * @param id the id of the view
     * @param viewLayout the new view state
     * @param onSuccess success callback, when the view has been successfully updated
     * @param onFailure failure callback, if the view has not been updated
     */
    abstract fun updView(
        id: String,
        viewLayout: ViewLayout,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    )

    /**
     * Get all views from a user
     *
     * @param userID the id of the user
     * @param onSuccess success callback, when the views have been successfully retrieved
     * @param onFailure failure callback, if the views have not been retrieved
     *
     * @return the views if they have been successfully retrieved, otherwise null
     */
    abstract suspend fun getViews(
        userID: String?,
        onSuccess: (String) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ): List<ViewLayout>

    /**
     * Get all views
     *
     * @return the views
     */
    abstract fun getAllViews(): Flow<List<ViewLayout>>

    // abstract flow lists //
    protected lateinit var projects: Flow<List<ProjectLayout>>
    protected lateinit var issues: Flow<List<IssueLayout>>
    protected lateinit var views: Flow<List<ViewLayout>>
    protected lateinit var users: Flow<List<UserLayout>>
}