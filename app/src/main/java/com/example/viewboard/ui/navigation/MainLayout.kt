package com.example.viewboard.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import com.example.viewboard.R

sealed class BottomBarScreen(val route: String, @StringRes val title: Int, val iconRes: Int) {
    object Home     : BottomBarScreen("home",      R.string.home,      R.drawable.house_black_silhouette_without_door_svgrepo_com)
    object Timetable: BottomBarScreen("timetable", R.string.timetable, R.drawable.calendar_mark_svgrepo_com)
    object View : BottomBarScreen("View",  R.string.views,  R.drawable.contacts_svgrepo_com)
    object Profile  : BottomBarScreen("profile",   R.string.profile,   R.drawable.user_svgrepo_com)
}

@Composable
fun MainLayout(
    navController: NavHostController,
    currentRoute: String?,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController, currentRoute = currentRoute)
        }
    ) { innerPadding ->
        content(innerPadding)
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
            contentColor = Color.Black,

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
                        if (current != screen.route ) {
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