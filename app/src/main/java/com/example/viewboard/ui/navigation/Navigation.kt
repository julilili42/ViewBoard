package com.example.viewboard.ui.navigation

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.viewboard.R
import com.example.viewboard.ui.screens.LoginScreen
import com.example.viewboard.ui.screens.RegistrationScreen
import com.example.viewboard.ui.screens.HomeScreen
import androidx.compose.material3.Divider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.viewboard.ui.screens.DragableScreen
import com.example.viewboard.ui.screens.IssueScreen
import com.example.viewboard.ui.issue.MainViewModel
import com.example.viewboard.ui.screens.HelpSupportScreen
import com.example.viewboard.ui.screens.ProjectsScreen
import com.example.viewboard.ui.screens.TimetableScreen
import com.example.viewboard.ui.screens.ProfileScreen
import com.example.viewboard.ui.screens.ViewScreen



@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideOn = listOf(
        Screen.LoginScreen.route,
        Screen.RegistrationScreen.route
    )
    val showBottomBar = currentRoute !in hideOn
    val mainViewModel = MainViewModel()

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.LoginScreen.route,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        ) {
            composable(Screen.LoginScreen.route) {
                LoginScreen(navController = navController)
            }
            composable(Screen.RegistrationScreen.route) {
                RegistrationScreen(navController = navController)
            }

            navigation(startDestination = BottomBarScreen.Home.route, route = "main") {
                composable(BottomBarScreen.Home.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        HomeScreen(navController = navController)
                    }
                }
                composable(BottomBarScreen.Timetable.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        TimetableScreen(navController = navController)
                    }
                }
                composable(BottomBarScreen.View.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        ViewScreen(navController = navController)
                    }
                }
                composable(BottomBarScreen.Profile.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        ProfileScreen(navController = navController)
                    }
                }
            }

            composable(BottomBarScreen.Profile.route) {
                MainLayout(navController, currentRoute) { padding ->
                    ProfileScreen(navController = navController)
                }
            }
            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(navArgument("projectName") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val projectName = backStackEntry.arguments!!.getString("projectName")!!
                MainLayout(navController, currentRoute) { padding ->
                    ProjectsScreen(navController = navController, projectName = projectName)
                }
            }
            composable(Screen.HelpSupportScreen.route) {
                MainLayout(navController, currentRoute) { padding ->
                    HelpSupportScreen(navController = navController)
                }
            }
            composable(
                route = Screen.IssueCreationScreen.route,
                arguments = listOf(navArgument("projectName") {
                    type = NavType.StringType
                })
            ) { backStack ->
                val projectName = backStack.arguments!!.getString("projectName")!!
                MainLayout(navController, currentRoute) { padding ->
                    DragableScreen(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        IssueScreen(mainViewModel, navController, projectName)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Timetable,
        BottomBarScreen.View,
        BottomBarScreen.Profile
    )

    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        Divider(
            color = Color.LightGray,
            thickness = 1.dp
        )
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val current = navBackStackEntry?.destination?.route

            items.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.iconRes),
                            contentDescription = stringResource(screen.title),
                            modifier      = Modifier.size(26.dp))
                    },
                    label = { Text(stringResource(screen.title)) },
                    selected = current == screen.route,
                    onClick = {
                        if (current != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}
