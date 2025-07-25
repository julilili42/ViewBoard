package com.example.viewboard.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
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
import com.example.viewboard.backend.storage.impl.FirebaseAPI
import com.example.viewboard.stateholder.IssueViewModel
import com.example.viewboard.ui.screens.auth.LoginScreen
import com.example.viewboard.ui.screens.auth.RegistrationScreen
import com.example.viewboard.ui.screens.home.HomeScreen
import com.example.viewboard.ui.screens.issue.DraggableScreen
import com.example.viewboard.ui.screens.issue.IssueScreen
import com.example.viewboard.stateholder.MainViewModel
import com.example.viewboard.stateholder.ProjectViewModel
import com.example.viewboard.stateholder.ViewsViewModel
import com.example.viewboard.ui.screens.profile.ChangeEmailScreen
import com.example.viewboard.ui.screens.profile.ChangePasswordScreen
import com.example.viewboard.ui.screens.profile.HelpSupportScreen
import com.example.viewboard.ui.screens.issue.IssueCreationScreen
import com.example.viewboard.ui.screens.issue.IssueEditScreen
import com.example.viewboard.ui.screens.project.ProjectEditScreen
import com.example.viewboard.ui.screens.project.ProjectsScreen
import com.example.viewboard.ui.screens.timetable.TimetableScreen
import com.example.viewboard.ui.screens.profile.ProfileScreen
import com.example.viewboard.ui.screens.project.ProjectCreationScreen
import com.example.viewboard.ui.screens.view.ViewIssueScreen
import com.example.viewboard.ui.screens.view.ViewScreen
import com.example.viewboard.backend.dataLayout.ProjectLayout

/**
 * Root of the app's navigation graph, deciding start destination based on auth state
 * and defining all navigation routes between composable screens.
 */
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isLoggedIn = FirebaseProvider.auth.currentUser != null
    val start = if (isLoggedIn) "main" else NavScreens.LoginNavScreens.route

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = start,
            modifier = Modifier.padding()

        ) {
            Log.d("Navigation", "start destination: $innerPadding")
            composable(NavScreens.LoginNavScreens.route) {
                LoginScreen(navController = navController)
            }
            composable(NavScreens.RegistrationNavScreens.route) {
                RegistrationScreen(navController = navController)
            }

            navigation(startDestination = BottomBarScreen.Home.route, route = "main") {
                composable(BottomBarScreen.Home.route) { backStack ->
                    val issueViewModel: IssueViewModel = viewModel(backStack)
                    val viewsViewModel: ViewsViewModel = viewModel(backStack)
                    val mainViewModel : MainViewModel = viewModel(backStack)
                    MainLayout(navController, currentRoute) { padding ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                        ) {
                            HomeScreen(
                                navController = navController,
                                viewModel = mainViewModel ,
                                modifier = Modifier,
                                issueViewModel = issueViewModel,
                                viewsViewModel = viewsViewModel,
                            )
                        }
                    }

                }

                composable(BottomBarScreen.Timetable.route) { backStack->
                    val issueViewModel: IssueViewModel = viewModel(backStack)
                    val projectViewModel: ProjectViewModel = viewModel(backStack)
                    MainLayout(navController, currentRoute) { padding ->
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                        ) {
                            TimetableScreen(navController = navController,
                                issueViewModel = issueViewModel,
                                projectViewModel = projectViewModel,)
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

            composable(BottomBarScreen.View.route) {backStack->
                val viewsViewModel: ViewsViewModel = viewModel(backStack)
                MainLayout(navController, currentRoute) { padding ->

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding)
                    ) {
                        ViewScreen(navController = navController,
                            viewsViewModel = viewsViewModel,
                        )
                    }
                }
            }

            composable(
                route = NavScreens.ProjectNavScreens.route,
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

            composable(route = NavScreens.HelpSupportNavScreens.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(padding)
                    ) {
                        HelpSupportScreen(navController = navController)
                    }
                }
            }

            composable(route = NavScreens.ChangePasswordNavScreens.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        ChangePasswordScreen(navController = navController)
                    }
                }
            }

            composable(route = NavScreens.ChangeEmailNavScreens.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        ChangeEmailScreen(navController = navController)
                    }
                }
            }

            composable(
                route = NavScreens.IssueEditNavScreens.route,
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
                route = "project/edit/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStack ->
                val id = backStack.arguments?.getString("projectId")!!
                var project by remember { mutableStateOf<ProjectLayout?>(null) }
                LaunchedEffect(id) {
                    project = FirebaseAPI.getProject(id)
                }
                if (project != null) {
                    ProjectEditScreen(
                        navController = navController,
                        projectId     = id,
                        project       = project!!,
                        onUpdated     = { /* optional: reload or navigate */ }
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            composable(
                route = NavScreens.IssueCreationNavScreens.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType }
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

            composable(route = NavScreens.ProjectCreationNavScreens.route) {
                MainLayout(navController, currentRoute) { padding ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                    ) {
                        ProjectCreationScreen(navController = navController)
                    }
                }
            }

            composable(
                route = NavScreens.IssueNavScreens.route,
                arguments = listOf(
                    navArgument("projectName") { type = NavType.StringType },
                    navArgument("projectId"  ) { type = NavType.StringType }
                )
            ) { backStack ->
                val projectName = backStack.arguments!!.getString("projectName")!!
                val projectId   = backStack.arguments!!.getString("projectId")!!
                val issueViewModel: IssueViewModel = viewModel(backStack)
                MainLayout(navController, currentRoute) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                    ) {
                        DraggableScreen(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            IssueScreen(
                                projectName  = projectName,
                                projectId    = projectId,
                                issueViewModel=issueViewModel ,
                                navController= navController,
                            )
                        }
                    }
                }
            }

            composable(
                route = NavScreens.ViewIssueNavScreens.route,
                arguments = listOf(
                    navArgument("viewID")   { type = NavType.StringType },
                    navArgument("projID")   { type = NavType.StringType }
                )
            ) { backStack ->
                val viewID   = backStack.arguments!!.getString("viewID")!!
                val projID   = backStack.arguments!!.getString("projID")!!
                val viewName = backStack.arguments!!.getString("viewName")!!
                val issueViewModel: IssueViewModel = viewModel(backStack)
                val projectViewModel: ProjectViewModel = viewModel(backStack)
                MainLayout(navController, currentRoute) { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(padding)
                    ) {
                        DraggableScreen(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            ViewIssueScreen(
                                viewId = viewID,
                                projectId = projID,
                                viewName = viewName,
                                issueViewModel = issueViewModel,
                                projectViewModel = projectViewModel,
                                navController = navController,

                            )
                        }
                    }
                }
            }
        }
    }
}
