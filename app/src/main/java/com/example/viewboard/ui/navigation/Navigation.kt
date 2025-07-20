package com.example.viewboard.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.viewboard.backend.auth.impl.FirebaseProvider
import com.example.viewboard.backend.dataLayout.IssueLayout
import com.example.viewboard.backend.storageServer.impl.FirebaseAPI
import com.example.viewboard.ui.issue.IssueViewModel
import com.example.viewboard.ui.screens.LoginScreen
import com.example.viewboard.ui.screens.RegistrationScreen
import com.example.viewboard.ui.screens.HomeScreen
import com.example.viewboard.ui.screens.DragableScreen
import com.example.viewboard.ui.screens.IssueScreen
import com.example.viewboard.ui.issue.MainViewModel
import com.example.viewboard.ui.issue.ProjectViewModel
import com.example.viewboard.ui.screens.ChangeEmailScreen
import com.example.viewboard.ui.screens.ChangePasswordScreen
import com.example.viewboard.ui.screens.HelpSupportScreen
import com.example.viewboard.ui.screens.IssueCreationScreen
import com.example.viewboard.ui.screens.IssueEditScreen
import com.example.viewboard.ui.screens.ProjectsScreen
import com.example.viewboard.ui.screens.TimetableScreen
import com.example.viewboard.ui.screens.ProfileScreen
import com.example.viewboard.ui.screens.ProjectCreationScreen
import com.example.viewboard.ui.screens.ViewIssueScreen
import com.example.viewboard.ui.screens.ViewScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainViewModel = MainViewModel()
    val isLoggedIn = FirebaseProvider.auth.currentUser != null
    val start = if (isLoggedIn) "main" else Screen.LoginScreen.route


    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = start,
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
                            .padding(padding)
                        ) {
                            HomeScreen(
                                navController = navController,
                                viewModel = mainViewModel ,
                                modifier = Modifier
                            )
                        }
                    }

                }
                composable(BottomBarScreen.Timetable.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                        ) {
                            TimetableScreen(navController = navController)
                        }
                    }
                }
                composable(BottomBarScreen.View.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                        ) {
                            ViewScreen(navController = navController)
                        }
                    }
                }
                composable(BottomBarScreen.Profile.route) {
                    MainLayout(navController, currentRoute) { padding ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
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
                        .padding(padding)
                    ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
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
                val projectViewModel: ProjectViewModel = viewModel(backStackEntry)
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding)
                    ) {
                        ProjectsScreen(navController = navController,
                            projectName = projectName,
                            projectViewModel = projectViewModel)
                    }
                }
            }
            composable(route = Screen.HelpSupportScreen.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding)
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
                route = Screen.IssueEditScreen.route,
                arguments = listOf(
                    navArgument("projectId") { type = NavType.StringType },
                    navArgument("issueId"  ) { type = NavType.StringType }
                )
            ) { backStack ->
                val projId = backStack.arguments!!.getString("projectId")!!
                val issueId = backStack.arguments!!.getString("issueId")!!
                var issue by remember { mutableStateOf<IssueLayout?>(null) }

                LaunchedEffect(issueId) {
                    issue = FirebaseAPI.getIssue(id = issueId)
                }

                if (issue != null) {
                    IssueEditScreen(
                        navController = navController,
                        projectId     = projId,
                        issue         = issue!!
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
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
                val issueViewModel: IssueViewModel = viewModel(backStack)
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
                                issueViewModel=issueViewModel ,
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
                    navArgument("viewID")   { type = NavType.StringType },
                    navArgument("projID")   { type = NavType.StringType }
                )
            ) { backStack ->
                val viewID   = backStack.arguments!!.getString("viewID")!!
                val projID   = backStack.arguments!!.getString("projID")!!
                val issueViewModel: IssueViewModel = viewModel(backStack)
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
                                mainViewModel = issueViewModel,
                                navController = navController,
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
