package com.puc.superid.ui.login

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
import androidx.navigation.NavController
import com.puc.superid.ui.passwordmanagement.GradientButton

@Composable
fun LoginScreen(navController: NavController, onLoginSuccess: () -> Unit) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = false
                        },
                        isError = emailError,
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
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = false
                        },
                        isError = passwordError,
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
                        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        val passwordValid = password.length >= 6

                        if (!emailValid) emailError = true
                        if (!passwordValid) passwordError = true

                        if (emailValid && passwordValid) {
                            FirebaseAuth.getInstance()
                                .signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = FirebaseAuth.getInstance().currentUser
                                        if (user?.isEmailVerified == true) {
                                            onLoginSuccess()
                                        } else {
                                            // Envia e-mail de verificação se não estiver verificado
                                            user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                                if (verifyTask.isSuccessful) {
                                                    errorMessage = "E-mail não verificado. Um novo e-mail de verificação foi enviado."
                                                } else {
                                                    errorMessage = "E-mail não verificado e ocorreu um erro ao enviar verificação."
                                                }
                                            }
                                        }
                                    } else {
                                        errorMessage = "E-mail ou senha incorretos."
                                    }
                                }
                        }
                    }

                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red)
                    }
                }
            }
        }
    }
}