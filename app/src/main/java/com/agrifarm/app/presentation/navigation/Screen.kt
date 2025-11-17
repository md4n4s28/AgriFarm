package com.agrifarm.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Person)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Marketplace : Screen("marketplace", "Market", Icons.Default.ShoppingCart)
    object Service : Screen("service", "Services", Icons.Default.Settings)
    object MyFarm : Screen("myfarm", "My Farm", Icons.Default.Grass)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object VoiceChat : Screen("voicechat", "AI Assistant", Icons.Default.Home)
    object IoTMonitor : Screen("iot", "IoT Monitor", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Marketplace,
    Screen.Service,
    Screen.MyFarm
)
