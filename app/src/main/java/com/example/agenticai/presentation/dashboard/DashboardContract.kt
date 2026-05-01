package com.example.agenticai.presentation.dashboard

import com.example.agenticai.presentation.navigation_screens.BottomNavItem

data class DashboardState (
    val selectedRoute: String = BottomNavItem.Home.route
)

sealed class DashboardIntent {
    data class OnTabSelected(val route: String) : DashboardIntent()
}