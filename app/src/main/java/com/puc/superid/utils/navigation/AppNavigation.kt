package com.puc.superid.utils.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.puc.superid.MainScreen
import com.puc.superid.ui.login.LoginScreen
import com.puc.superid.ui.passwordmanagement.PasswordRecoveryScreen
import com.puc.superid.MainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainScreen()
        }
        composable("recover") {
            PasswordRecoveryScreen(
                navController = navController,
                onLinkSent = {
                    navController.popBackStack()
                }
            )
        }
    }
}