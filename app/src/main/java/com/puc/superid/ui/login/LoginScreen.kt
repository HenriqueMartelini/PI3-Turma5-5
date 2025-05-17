package com.puc.superid.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.*
import androidx.compose.ui.graphics.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.puc.superid.MainActivity
import com.puc.superid.utils.navigation.AppNavigation
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.viewmodel.LoginViewModel


class LoginScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SuperidTheme {
                AppNavigation()
            }
        }
    }
}



@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel()
    val uiState = viewModel.uiState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0B2D), Color(0xFF1C2D69))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Entrar",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Reenviar e-mail de verificação",
                color = Color(0xFFB2EBF2),
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .clickable {
                        viewModel.resendVerificationToEmail(
                            uiState.email,
                            uiState.password
                        ) { message ->
                            viewModel.uiState = viewModel.uiState.copy(errorMessage = message)
                        }
                    }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3B3F76)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Email", color = Color(0xFFB2EBF2), modifier = Modifier.align(Alignment.Start))
                    TextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        isError = uiState.emailError,
                        placeholder = { Text("exemplo@gmail.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Text("Senha", color = Color(0xFFB2EBF2), modifier = Modifier.align(Alignment.Start))
                    TextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        isError = uiState.passwordError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Esqueci minha senha",
                        color = Color.Black,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .clickable {
                                navController.navigate("recover")
                            }
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        text = "Entrar",
                        gradient = Brush.horizontalGradient(
                            listOf(Color(0xFF2D49FB), Color(0xFF618CFF))
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        viewModel.login(
                            onSuccess = {
                                onLoginSuccess()
                            },
                            onFailure = { message ->
                                viewModel.uiState = viewModel.uiState.copy(errorMessage = message)
                            }
                        )
                    }

                    uiState.errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red)
                    }
                }
            }
        }
    }
}


@Composable
fun GradientButton(text: String, gradient: Brush, modifier: Modifier, content: () -> Unit) {
    Button(
        onClick = { content() },
        modifier = modifier
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient, shape = RoundedCornerShape(24.dp))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, style = MaterialTheme.typography.labelLarge)
        }
    }
}