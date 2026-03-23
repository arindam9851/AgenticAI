package com.example.agenticai.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agenticai.presentation.cart.CartScreen
import com.example.agenticai.presentation.chat.ChatScreen
import com.example.agenticai.presentation.order.OrderScreen

// ── Routes ────────────────────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Chat : Screen("chat")
    object Cart : Screen("cart")
    object Orders : Screen("orders")
}

// ── NavGraph ──────────────────────────────────────────────────────────────────
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Chat.route
    ) {
        composable(Screen.Chat.route) {
            ChatScreen(
                onCartClick = {
                    navController.navigate(Screen.Cart.route) {
                        // Avoid multiple copies of cart on back stack
                        launchSingleTop = true
                    }
                },
                onOrderClick = {
                    navController.navigate(Screen.Orders.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Orders.route) {
            OrderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}