package com.puc.superid.ui.passwordmanagement

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.viewmodel.EditPasswordViewModel

class EditPasswordActivity : ComponentActivity() {

    companion object {
        const val EXTRA_DOCUMENT_ID = "document_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val documentId = intent.getStringExtra(EXTRA_DOCUMENT_ID) ?: ""

        setContent {
            SuperidTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "editPassword"
                ) {
                    composable("editPassword") {
                        EditPasswordScreen(
                            documentId = documentId,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasswordScreen(
    documentId: String,
    navController: NavController,
    viewModel: EditPasswordViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    var currentEmail by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }

    var newEmail by remember { mutableStateOf("") }
    var repeatNewEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var repeatNewPassword by remember { mutableStateOf("") }

    LaunchedEffect(documentId) {
        if (documentId.isNotBlank()) {
            viewModel.loadPasswordData(documentId)
        }
    }

    LaunchedEffect(uiState) {
        currentEmail = uiState.username
        currentPassword = uiState.password

        newEmail = uiState.username
        repeatNewEmail = uiState.username
        newPassword = uiState.password
        repeatNewPassword = uiState.password
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D0B2D), Color(0xFF0033FF))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(documentId, style = MaterialTheme.typography.headlineSmall, color = Color.White)

            Spacer(modifier = Modifier.height(24.dp))

            // CARD SUPERIOR - EMAIL E SENHA ATUAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.2f))
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Email", color = Color.Black)
                    Text(currentEmail, color = Color.Black)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Senha", color = Color.Black)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (passwordVisible) currentPassword else "*".repeat(currentPassword.length),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Done else Icons.Filled.Lock,
                                contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Column(
                    Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Text("Novo email", color = Color.Cyan)
                    TextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("ex: novomail@email.com") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Repetir novo email", color = Color.Cyan)
                    TextField(
                        value = repeatNewEmail,
                        onValueChange = { repeatNewEmail = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Nova senha", color = Color.Cyan)
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Repetir nova senha", color = Color.Cyan)
                    TextField(
                        value = repeatNewPassword,
                        onValueChange = { repeatNewPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (newEmail == repeatNewEmail && newPassword == repeatNewPassword) {
                        viewModel.onFieldChange(
                            title = uiState.title,
                            username = if (newEmail.isNotBlank()) newEmail else uiState.username,
                            password = if (newPassword.isNotBlank()) newPassword else uiState.password,
                            category = uiState.category
                        )
                        viewModel.updatePassword(
                            onSuccess = {
                                Toast.makeText(context, "Senha atualizada!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Emails ou senhas não coincidem", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3366FF))
            ) {
                Text("Redefinir acesso", color = Color.White)
            }
        }
    }
}