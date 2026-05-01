package com.example.agenticai.presentation.navigation_screens


sealed class Screen(val route: String) {
    object DashBoard : Screen("dashboard")
    object Splash : Screen("splash")
}