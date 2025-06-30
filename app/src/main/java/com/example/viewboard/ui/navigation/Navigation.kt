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
import com.example.viewboard.ui.screens.ProfileScreen
import com.example.viewboard.ui.screens.ProjectsScreen
import com.example.viewboard.ui.screens.TimetableScreen
import com.example.viewboard.ui.screens.ViewScreen

sealed class BottomBarScreen(val route: String, @StringRes val title: Int, val iconRes: Int) {
    object Home     : BottomBarScreen("home",      R.string.home,      R.drawable.house_black_silhouette_without_door_svgrepo_com)
    object Timetable: BottomBarScreen("timetable", R.string.timetable, R.drawable.calendar_mark_svgrepo_com)
    object View : BottomBarScreen("View",  R.string.views,  R.drawable.contacts_svgrepo_com)
    object Profile  : BottomBarScreen("profile",   R.string.profile,   R.drawable.user_svgrepo_com)
}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideOn = listOf(
        Screen.LoginScreen.route,
        Screen.RegistrationScreen.route,
        Screen.HelpSupportScreen.route
    )
    val showBottomBar = currentRoute !in hideOn
    val mainViewModel = MainViewModel()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.LoginScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.LoginScreen.route) {
                LoginScreen(navController = navController)
            }
            composable(Screen.RegistrationScreen.route) {
                RegistrationScreen(navController = navController)
            }
            composable(Screen.HelpSupportScreen.route) {
                HelpSupportScreen(navController = navController)
            }
            navigation(
                startDestination = BottomBarScreen.Home.route,
                route = "main"
            ) {
                composable(BottomBarScreen.Home.route) {
                    HomeScreen(navController = navController)
                }
                composable(BottomBarScreen.Timetable.route) {
                    TimetableScreen(navController = navController)
                }
                composable(BottomBarScreen.View.route) {
                    ViewScreen(navController = navController)
                }
                composable(BottomBarScreen.Profile.route) {
                    ProfileScreen(navController = navController)
                }
            }
            composable(
                route = Screen.ProjectDetail.route,
                arguments = listOf(navArgument("projectName"){
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val projectName = backStackEntry.arguments!!.getString("projectName")!!
                ProjectsScreen(
                    navController = navController,
                    projectName = projectName,
                )
            }
            composable(
                route = Screen.IssueCreationScreen.route,
                arguments = listOf(navArgument("projectName") { type = NavType.StringType })
            ) { backStack ->
                val projectName = backStack.arguments!!.getString("projectName")!!
                DragableScreen(
                    modifier = Modifier.fillMaxSize()
                ) {
                    IssueScreen(mainViewModel,navController)
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
