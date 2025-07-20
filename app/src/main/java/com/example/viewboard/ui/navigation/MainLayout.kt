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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.compose.material3.Scaffold
import com.example.viewboard.R

sealed class BottomBarScreen(val route: String, @StringRes val title: Int, val iconRes: Int) {
    object Home : BottomBarScreen(
        "home",
        R.string.home,
        R.drawable.house_black_silhouette_without_door_svgrepo_com
    )
    object Timetable :
        BottomBarScreen("timetable", R.string.timetable, R.drawable.calendar_mark_svgrepo_com)
    object View : BottomBarScreen("view", R.string.views, R.drawable.contacts_svgrepo_com)
    object Profile : BottomBarScreen("profile", R.string.profile, R.drawable.user_svgrepo_com)
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
    Column(
        modifier = Modifier
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
            items.forEach { screen ->
                val selected = currentRoute?.startsWith(screen.route) == true
                NavigationBarItem(
                    selected = selected,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor        = Color(0xFF795548), // z.B. Braun
                        selectedIconColor     = Color.White,
                        selectedTextColor     = Color.White,
                        unselectedIconColor   = Color(0xFF757575),
                        unselectedTextColor   = Color(0xFF757575)
                    ),
                    icon = {
                        Icon(
                            painter = painterResource(id = screen.iconRes),
                            contentDescription = stringResource(screen.title),
                            modifier = Modifier.size(26.dp),
                            tint = if (selected) Color.White else Color(0xFF757575)
                        )
                    },
                    label = {
                        Text(
                            stringResource(screen.title),
                            color = if (selected) Color(0xFF212121) else Color(0xFF757575)
                        )
                    },
                    onClick = {

                        if (currentRoute != screen.route) {
                            navController.navigateTab(screen.route)
                        }
                    }
                )
            }
        }
    }
}

fun NavHostController.navigateTab(route: String) {
    this.navigate(route) {
        popUpTo("main") {
            inclusive = false
        }
        launchSingleTop = true
        restoreState = false
    }
}