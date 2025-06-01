package com.example.viewboard.ui.navigation

sealed class Screen(val route: String) {
    object LoginScreen: Screen(route = "login")
    object RegistrationScreen: Screen(route = "registration")
    object HomeScreen: Screen(route = "home")
}