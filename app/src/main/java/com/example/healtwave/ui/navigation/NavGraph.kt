package com.example.healtwave.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.healtwave.ui.dashboard.DashboardScreen
import com.example.healtwave.ui.login.LoginScreen
import com.example.healtwave.ui.register.RegisterScreen
import com.example.healtwave.ui.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashEnd = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = { userName ->
                    navController.navigate("dashboard/$userName")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("dashboard/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            DashboardScreen(
                userName = userName,
                onLogout = {
                    navController.popBackStack("login", inclusive = true)
                }
            )
        }
    }
}