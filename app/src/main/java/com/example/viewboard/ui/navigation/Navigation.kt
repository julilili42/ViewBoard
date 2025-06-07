package com.example.viewboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.viewboard.ui.navigation.Screen
import com.example.viewboard.ui.account.LoginScreen
import com.example.viewboard.ui.account.RegistrationScreen
import com.example.viewboard.ui.home.HomeScreen
import com.example.viewboard.ui.home.ProjectDetailScreen

/**
 * Sets up the app’s navigation graph using Jetpack Compose Navigation.
 *
 * @param modifier optional [Modifier] for styling or layout adjustments
 */
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController();

    NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {

        /** Login screen route */
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }

        /** Home screen route */
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController = navController)
        }

        /** Registration screen route */
        composable(route = Screen.RegistrationScreen.route) {
            RegistrationScreen(navController = navController)
        }

        /**
         * Detail screen for a specific project.
         * Expects a String argument "projectName"
         */
        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(navArgument("projectName") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val name = backStackEntry.arguments!!.getString("projectName")!!
            ProjectDetailScreen(projectName = name, navController = navController)
        }
    }
}