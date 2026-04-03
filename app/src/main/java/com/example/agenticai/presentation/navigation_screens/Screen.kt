package com.example.agenticai.presentation.navigation_screens


sealed class Screen(val route: String) {
    object Chat : Screen("chat")
    object Cart : Screen("cart")
    object Orders : Screen("orders")
    object Splash : Screen("splash")
}