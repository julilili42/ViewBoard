package com.example.viewboard.frontend.navigation

/**
 * Defines all navigation routes in the app.
 * Each Screen object holds its unique route string.
 */
sealed class NavScreens(val route: String) {
    /** Screen for the login page */
    object LoginNavScreens : NavScreens(route = "login")

    /** Screen for the user registration page */
    object RegistrationNavScreens : NavScreens(route = "registration")

    /** Screen for the main/home page */
    object HomeNavScreens : NavScreens(route = "home")

    object ProjectNavScreens : NavScreens("home/project/{projectName}") {

        fun createRoute(projectName: String) = "home/project/$projectName"
    }

    /** Screen for viewing all issues of a specific project */
    object IssueNavScreens : NavScreens("home/project/{projectName}/issue/{projectId}") {
        fun createRoute(projectName: String, projectId: String) =
            "home/project/$projectName/issue/$projectId"
    }

    /** Screen for creating a new issue in a project */
    object IssueCreationNavScreens :
        NavScreens("home/project/{projectName}/issueCreation/{projectId}") {
        fun createRoute(projectName: String, projectId: String) =
            "home/project/$projectName/issueCreation/$projectId"
    }

    /** Screen for editing an existing issue */
    object IssueEditNavScreens : NavScreens("home/project/issueEdit/{projectId}/{issueId}") {
        fun createRoute(projectId: String, issueId: String) =
            "home/project/issueEdit/$projectId/$issueId"
    }

    /** Screen for creating a new project */
    object ProjectCreationNavScreens : NavScreens("home/project/{projectName}/projectsCreation")

    /** Screen for viewing a specific issue from a shared view */
    object ViewIssueNavScreens : NavScreens("views/viewIssue/{viewID}/{projID}/{viewName}") {
        fun createRoute(viewID: String, projID: String, viewName: String) =
            "views/viewIssue/$viewID/$projID/$viewName"
    }

    /** Screen for changing password (accessed via profile) */
    object ChangePasswordNavScreens : NavScreens("profile/passwordChange")

    /** Screen for changing email (accessed via profile) */
    object ChangeEmailNavScreens : NavScreens("profile/emailChange")

    /** Screen for help & support (accessed via profile) */
    object HelpSupportNavScreens : NavScreens("profile/helpSupport")
}