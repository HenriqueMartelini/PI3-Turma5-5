package com.puc.superid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.puc.superid.ui.navigation.AppNavigation
import com.puc.superid.ui.theme.SuperidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperidTheme {
                // Cria o navController
                val navController = rememberNavController()

                // Superfície para aplicar tema e definir o conteúdo
                Surface(color = MaterialTheme.colorScheme.background) {
                    // Passa o navController para a função de navegação
                    AppNavigation(navController)
                }
            }
        }
    }
}