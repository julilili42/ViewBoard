package com.example.viewboard.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.viewboard.ui.screens.LoginScreen
import com.example.viewboard.ui.screens.RegistrationScreen
import com.example.viewboard.ui.screens.HomeScreen
import com.example.viewboard.ui.screens.DragableScreen
import com.example.viewboard.ui.screens.IssueScreen
import com.example.viewboard.ui.issue.MainViewModel
import com.example.viewboard.ui.screens.ChangeEmailScreen
import com.example.viewboard.ui.screens.ChangePasswordScreen
import com.example.viewboard.ui.screens.HelpSupportScreen
import com.example.viewboard.ui.screens.IssueCreationScreen
import com.example.viewboard.ui.screens.ProjectsScreen
import com.example.viewboard.ui.screens.TimetableScreen
import com.example.viewboard.ui.screens.ProfileScreen
import com.example.viewboard.ui.screens.ProjectCreationScreen
import com.example.viewboard.ui.screens.ViewIssueScreen
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
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding) // ✅ korrekt
                        ) {
                            HomeScreen(
                                navController = navController,
                                modifier = Modifier
                            )
                        }
                    }

                }
                composable(BottomBarScreen.Timetable.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding) // ✅ korrekt
                        ) {
                            TimetableScreen(navController = navController)
                        }
                    }
                }
                composable(BottomBarScreen.View.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding) // ✅ korrekt
                        ) {
                            ViewScreen(navController = navController)
                        }
                    }
                }
                composable(BottomBarScreen.Profile.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding) // ✅ korrekt
                        ) {
                            ProfileScreen(navController = navController)
                        }
                    }
                }
            }

            composable(BottomBarScreen.Profile.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding) // ✅ korrekt
                    ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding) // ✅ korrekt
                    ) {
                        ProfileScreen(navController = navController)
                    }
                }
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
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding) // ✅ korrekt
                    ) {
                        ProjectsScreen(navController = navController, projectName = projectName)
                    }
                }
            }
            composable(route = Screen.HelpSupportScreen.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding) // ✅ korrekt
                    ) {
                        HelpSupportScreen(navController = navController)
                    }
                }
            }
            composable(route = Screen.ChangePasswordScreen.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        ChangePasswordScreen(navController = navController)
                    }
                }
            }

            composable(route = Screen.ChangeEmailScreen.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        ChangeEmailScreen(navController = navController)
                    }
                }
            }

            composable(
                route = Screen.IssueCreationScreen.route,
                arguments = listOf(
                    navArgument("projectId") {
                        type = NavType.StringType
                    }
                )
            ) { backStack ->
                val projectId = backStack.arguments!!.getString("projectId")!!

                MainLayout(navController, currentRoute) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                    ) {
                        IssueCreationScreen(
                            navController = navController,
                            projectId    = projectId
                        )
                    }
                }
            }
            composable(route = Screen.ProjectCreationScreen.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {

                        ProjectCreationScreen(navController = navController)

                    }
                }
            }
            composable(
                route = Screen.IssueScreen.route,
                arguments = listOf(
                    navArgument("projectName") { type = NavType.StringType },
                    navArgument("projectId"  ) { type = NavType.StringType }
                )
            ) { backStack ->
                // beide Argumente auslesen
                val projectName = backStack.arguments!!.getString("projectName")!!
                val projectId   = backStack.arguments!!.getString("projectId")!!
                MainLayout(navController, currentRoute) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                    ) {
                        DragableScreen(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            IssueScreen(
                                mainViewModel,
                                navController,
                                projectName  = projectName,
                                projectId    = projectId   // jetzt mit übergeben
                            )
                        }
                    }
                }
            }
            composable(
                route = Screen.ViewIssueScreen.route,
                arguments = listOf(
                    navArgument("viewName") { type = NavType.StringType },
                    navArgument("viewID")   { type = NavType.StringType },
                    navArgument("projID")   { type = NavType.StringType }
                )
            ) { backStack ->
                val viewName = backStack.arguments!!.getString("viewName")!!
                val viewID   = backStack.arguments!!.getString("viewID")!!
                val projID   = backStack.arguments!!.getString("projID")!!
                MainLayout(navController, currentRoute) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                    ) {
                        DragableScreen(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            ViewIssueScreen(
                                mainViewModel = mainViewModel,
                                navController = navController,
                                viewName = viewName,
                                viewID = viewID,
                                projID = projID
                            )
                        }
                    }
                }
            }
        }
    }
}
