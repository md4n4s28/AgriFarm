package com.agrifarm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.agrifarm.app.presentation.home.HomeScreen
import com.agrifarm.app.presentation.marketplace.MarketplaceScreen
import com.agrifarm.app.presentation.myfarm.MyFarmScreen
import com.agrifarm.app.presentation.myfarm.NewMyFarmScreen
import com.agrifarm.app.presentation.navigation.Screen
import com.agrifarm.app.presentation.navigation.bottomNavItems
import com.agrifarm.app.presentation.profile.ProfileScreen
import com.agrifarm.app.presentation.service.ServiceScreen
import com.agrifarm.app.presentation.iot.IotPackageSelectionScreen
import com.agrifarm.app.presentation.voicechat.VoiceChatScreen
import com.agrifarm.app.presentation.auth.LoginScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgriFarmApp()
        }
    }
}

@Composable
fun AgriFarmApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Login.route) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50)
                ) {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) {
                                            inclusive = screen.route == Screen.Home.route
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF4CAF50),
                                selectedTextColor = Color(0xFF4CAF50),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFFE8F5E9)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLoginSuccess = { userId, email, name ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) { 
                HomeScreen(
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    },
                    onVoiceChatClick = {
                        navController.navigate(Screen.VoiceChat.route)
                    },
                    onIoTClick = {
                        navController.navigate(Screen.IoTMonitor.route)
                    }
                )
            }
            composable(Screen.Marketplace.route) { MarketplaceScreen() }
            composable(Screen.Service.route) { 
                ServiceScreen(
                    onIotMonitoringClick = {
                        navController.navigate("iot_packages")
                    }
                )
            }
            composable("iot_packages") {
                IotPackageSelectionScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.MyFarm.route) { NewMyFarmScreen() }
            composable(Screen.Profile.route) { 
                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.VoiceChat.route) {
                VoiceChatScreen(onBackClick = {
                    navController.popBackStack()
                })
            }
            composable(Screen.IoTMonitor.route) {
                IotPackageSelectionScreen(onBackClick = {
                    navController.popBackStack()
                })
            }
        }
    }
}
