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

    /** Screen for the help/support page, hides bottomBar */
    object HelpSupportScreen: Screen(route= "helpSupport")

    /**
     * Screen for displaying details of a specific project.
     * Uses a placeholder `{projectName}` which will be replaced at runtime.
     *
     * @param projectName the name (or ID) of the project to display
     */
    object ProjectDetail : Screen("project/{projectName}") {

        /**
         * Helper to build a concrete route string.
         *
         * @param projectName the name (or ID) of the project
         * @return the route with the placeholder replaced, e.g. "project/MyProject"
         */
        fun createRoute(projectName: String) = "project/$projectName"
    }

    /** Screen for the adding an Issue */
    object IssueCreationScreen : Screen(route = "issueCreation{projectId}"){
        fun createRoute(projectId: String) =
            "issueCreation{$projectId}"
    }
    /** Screen for the adding an Projects */
    object ProjectCreationScreen : Screen(route = "projectsCreation")

    object IssueScreen : Screen("issue/{projectName}/{projectId}"){

        // Hilfsfunktion, um den Navigations‐String zu bauen
        fun createRoute(projectName: String, projectId: String) =
            "issue/$projectName/$projectId"
    }
}