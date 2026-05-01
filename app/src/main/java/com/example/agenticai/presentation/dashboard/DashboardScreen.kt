package com.example.agenticai.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.agenticai.presentation.home.HomeScreen
import com.example.agenticai.presentation.navigation_screens.BottomNavItem

@Composable
fun DashboardScreen(
    // Pass your existing screen composables as lambdas
    chatScreen: @Composable () -> Unit,
    cartScreen: @Composable () -> Unit,
    orderScreen: @Composable () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Chat,
        BottomNavItem.Cart,
        BottomNavItem.Orders,
        BottomNavItem.Profile,
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = state.selectedRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            viewModel.processIntent(DashboardIntent.OnTabSelected(item.route))
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    // Show badge on cart tab
                                    if (item is BottomNavItem.Cart) {
                                        Badge()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(text = item.label, fontSize = 10.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSurface,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.selectedRoute) {
            BottomNavItem.Home.route -> HomeScreen(paddingValues = paddingValues)
            BottomNavItem.Chat.route -> chatScreen()
            BottomNavItem.Cart.route -> cartScreen()
            BottomNavItem.Orders.route -> orderScreen()
            BottomNavItem.Profile.route -> ProfilePlaceholderTab()
        }
    }
}

// Temporary placeholder for Profile
@Composable
fun ProfilePlaceholderTab() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "Profile — Coming soon",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}