package com.example.viewboard.ui.navigation

/**
 * Defines all navigation routes in the app.
 * Each Screen object holds its unique route string.
 */
sealed class Screen(val route: String) {
    /** Screen for the login page */
    object LoginScreen : Screen(route = "login")

    /** Screen for the user registration page */
    object RegistrationScreen : Screen(route = "registration")

    /** Screen for the main/home page */
    object HomeScreen : Screen(route = "home")

    object ProjectScreen : Screen("home/project/{projectName}") {

        fun createRoute(projectName: String) = "home/project/$projectName"
    }
    /*
        /** Screen for the help/support page, hides bottomBar */
        object HelpSupportScreen: Screen(route= "helpSupport")

        /** Screen for changing the password */
        object ChangePasswordScreen: Screen(route= "passwordChange")

        /** Screen for changing the email */
        object ChangeEmailScreen: Screen(route= "emailChange")

        /**
         * Screen for displaying details of a specific project.
         * Uses a placeholder `{projectName}` which will be replaced at runtime.
         *
         * @param projectName the name (or ID) of the project to display
         */


            /** Screen for the adding an Issue */
            object IssueCreationScreen : Screen(route = "issueCreation{projectId}"){
                fun createRoute(projectId: String) = "issueCreation{$projectId}"
            }

            object IssueEditScreen : Screen("issueEdit/{projectId}/{issueId}") {
                fun createRoute(projectId: String, issueId: String) =
                    "issueEdit/$projectId/$issueId"
            }

            /** Screen for the adding an Projects */
            object ProjectCreationScreen : Screen(route = "projectsCreation")

            object IssueScreen : Screen("issue/{projectName}/{projectId}"){
                // Hilfsfunktion, um den Navigations‐String zu bauen
                fun createRoute(projectName: String, projectId: String) = "issue/$projectName/$projectId"
            }

            object ViewIssueScreen : Screen("viewIssue/{viewID}/{projID}"){
                fun createRoute(viewID: String, projID: String) = "viewIssue/$viewID/$projID"
            }*/
    /** Screen for viewing all issues of a specific project */
    object IssueScreen : Screen("home/project/{projectName}/issue/{projectId}") {
        fun createRoute(projectName: String, projectId: String) =
            "home/project/$projectName/issue/$projectId"
    }

    /** Screen for creating a new issue in a project */
    object IssueCreationScreen : Screen("home/project/{projectName}/issueCreation/{projectId}") {
        fun createRoute(projectName: String, projectId: String) =
            "home/project/$projectName/issueCreation/$projectId"
    }

    /** Screen for editing an existing issue */
    object IssueEditScreen : Screen("home/project/{projectName}/issueEdit/{projectId}/{issueId}") {
        fun createRoute(projectName: String, projectId: String, issueId: String) =
            "home/project/$projectName/issueEdit/$projectId/$issueId"
    }

    /** Screen for creating a new project */
    object ProjectCreationScreen : Screen("home/project/{projectName}/projectsCreation") {
        fun createRoute(projectName: String) =
            "home/project/$projectName/projectsCreation"
    }

    /** Screen for viewing a specific issue from a shared view */
    object ViewIssueScreen : Screen("views/viewIssue/{viewID}/{projID}/{viewName}") {
        fun createRoute(viewID: String, projID: String, viewName: String) =
            "views/viewIssue/$viewID/$projID/$viewName"
    }

    /** Screen for changing password (accessed via profile) */
    object ChangePasswordScreen : Screen("profile/passwordChange")

    /** Screen for changing email (accessed via profile) */
    object ChangeEmailScreen : Screen("profile/emailChange")

    /** Screen for help & support (accessed via profile) */
    object HelpSupportScreen : Screen("profile/helpSupport")


}