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
}