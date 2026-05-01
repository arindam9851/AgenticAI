package com.example.agenticai.presentation.navigation_screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agenticai.presentation.cart.CartScreen
import com.example.agenticai.presentation.chat.ChatScreen
import com.example.agenticai.presentation.dashboard.DashboardScreen
import com.example.agenticai.presentation.order.OrderScreen
import com.example.agenticai.presentation.splash.SplashScreen



// ── NavGraph ──────────────────────────────────────────────────────────────────
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        // ── Splash ────────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToDashBoard = {
                    navController.navigate(Screen.DashBoard.route) {
                        // Remove splash from back stack so back button doesn't return to it
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable (Screen.DashBoard.route){
            DashboardScreen(
                chatScreen = {ChatScreen ()},
                cartScreen = {CartScreen(
                    onNavigateBack = { navController.popBackStack() }
                )},
                orderScreen = {OrderScreen(
                    onNavigateBack = { navController.popBackStack() }
                )}

            )

        }
        // ── Chat ────────────────────────────────────────────────────────────
//        composable(Screen.Chat.route) {
//            ChatScreen(
//                onCartClick = {
//                    navController.navigate(Screen.Cart.route) {
//                        // Avoid multiple copies of cart on back stack
//                        launchSingleTop = true
//                    }
//                },
//                onOrderClick = {
//                    navController.navigate(Screen.Orders.route) {
//                        launchSingleTop = true
//                    }
//                }
//            )
//        }
//        // ── Cart ────────────────────────────────────────────────────────────
//        composable(Screen.Cart.route) {
//            CartScreen(
//                onNavigateBack = {
//                    navController.popBackStack()
//                }
//            )
//        }
//        // ── Order ────────────────────────────────────────────────────────────
//        composable(Screen.Orders.route) {
//            OrderScreen(
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }

    }
}