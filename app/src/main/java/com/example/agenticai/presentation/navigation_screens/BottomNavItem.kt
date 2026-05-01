package com.example.agenticai.presentation.navigation_screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem (
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
){
    object Home : BottomNavItem(
        route = "home",
        label = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )
    object Chat : BottomNavItem(
        route = "chat",
        label = "AI Assistance",
        icon = Icons.Outlined.Chat,
        selectedIcon = Icons.Filled.Chat
    )
    object Cart : BottomNavItem(
        route = "cart",
        label = "Cart",
        icon = Icons.Outlined.ShoppingCart,
        selectedIcon = Icons.Filled.ShoppingCart
    )
    object Orders : BottomNavItem(
        route = "orders",
        label = "Orders",
        icon = Icons.Outlined.Receipt,
        selectedIcon = Icons.Filled.Receipt
    )
    object Profile : BottomNavItem(
        route = "profile",
        label = "Profile",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )
}