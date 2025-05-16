package com.puc.superid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.puc.superid.ui.login.LoginScreen
import com.puc.superid.ui.login.MainPage
import com.puc.superid.ui.passwordmanagement.PasswordRecoveryScreen

@Composable
fun AppNavigation(navController: NavController) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = { navController.navigate("main") }
            )
        }
        composable("main") {
            MainPage()
        }
        composable("recover") {
            PasswordRecoveryScreen(
                navController = navController,
                onLinkSent = {
                    // Navega de volta para a tela anterior após o envio
                    navController.popBackStack()
                }
            )
        }
    }
}