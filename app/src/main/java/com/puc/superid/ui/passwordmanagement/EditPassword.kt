package com.puc.superid.ui.passwordmanagement

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.utils.StringUtils.isValidEmail
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

    var isEditingEmail by remember { mutableStateOf(false) }
    var isEditingPassword by remember { mutableStateOf(false) }

    var newEmail by remember { mutableStateOf("") }
    var confirmNewEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    val isEmailValid by remember(newEmail, confirmNewEmail) {
        derivedStateOf {
            !isEditingEmail || (
                    newEmail.isNotBlank() &&
                            confirmNewEmail.isNotBlank() &&
                            newEmail == confirmNewEmail &&
                            isValidEmail(newEmail)
                    )
        }
    }

    val isPasswordValid by remember(newPassword, confirmNewPassword) {
        derivedStateOf {
            !isEditingPassword || (newPassword.isNotBlank() && confirmNewPassword.isNotBlank() && newPassword == confirmNewPassword)
        }
    }

    val canSave by remember(isEditingEmail, isEditingPassword, isEmailValid, isPasswordValid) {
        derivedStateOf {
            (isEditingEmail || isEditingPassword) &&
                    (!isEditingEmail || isEmailValid) &&
                    (!isEditingPassword || isPasswordValid)
        }
    }

    LaunchedEffect(documentId) {
        if (documentId.isNotBlank()) {
            viewModel.loadPasswordData(documentId)
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D0B2D), Color(0xFF0033FF))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SuperID",
                        color = Color.White,
                        modifier = Modifier.clickable {
                            navController.navigate("mainScreen") {
                                popUpTo("mainScreen") { inclusive = true }
                            }
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Editar Credenciais de $documentId",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                        Text("Email atual", color = Color.White)
                        Text(
                            text = uiState.username.ifEmpty { "Carregando..." },
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Senha atual", color = Color.White)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (passwordVisible) uiState.password else "*".repeat(6),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Done else Icons.Filled.Lock,
                                    contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                                    tint = Color.White
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isEditingEmail,
                                onCheckedChange = {
                                    isEditingEmail = it
                                    if (!it) {
                                        newEmail = ""
                                        confirmNewEmail = ""
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Cyan,
                                    uncheckedColor = Color.White
                                )
                            )
                            Text("Editar email", color = Color.White)
                        }

                        if (isEditingEmail) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Novo email", color = Color.Cyan)
                            TextField(
                                value = newEmail,
                                onValueChange = { newEmail = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("ex: novomail@email.com", color = Color.Gray) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                isError = newEmail.isNotBlank() && confirmNewEmail.isNotBlank() && newEmail != confirmNewEmail
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Confirmar novo email", color = Color.Cyan)
                            TextField(
                                value = confirmNewEmail,
                                onValueChange = { confirmNewEmail = it },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                isError = newEmail.isNotBlank() && confirmNewEmail.isNotBlank() && newEmail != confirmNewEmail
                            )

                            if (newEmail.isNotBlank() && confirmNewEmail.isNotBlank() && newEmail != confirmNewEmail) {
                                Text(
                                    text = "Os emails não coincidem",
                                    color = Color.Red,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            if (newEmail.isNotBlank() && !isValidEmail(newEmail)) {
                                Text(
                                    text = "Formato de e-mail inválido",
                                    color = Color.Red,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isEditingPassword,
                                onCheckedChange = {
                                    isEditingPassword = it
                                    if (!it) {
                                        newPassword = ""
                                        confirmNewPassword = ""
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Cyan,
                                    uncheckedColor = Color.White
                                )
                            )
                            Text("Editar senha", color = Color.White)
                        }

                        if (isEditingPassword) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Nova senha", color = Color.Cyan)
                            TextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                isError = newPassword.isNotBlank() && confirmNewPassword.isNotBlank() && newPassword != confirmNewPassword
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Confirmar nova senha", color = Color.Cyan)
                            TextField(
                                value = confirmNewPassword,
                                onValueChange = { confirmNewPassword = it },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                isError = newPassword.isNotBlank() && confirmNewPassword.isNotBlank() && newPassword != confirmNewPassword
                            )

                            if (newPassword.isNotBlank() && confirmNewPassword.isNotBlank() && newPassword != confirmNewPassword) {
                                Text(
                                    text = "As senhas não coincidem",
                                    color = Color.Red,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (!canSave) return@Button

                        viewModel.updateCredentials(
                            newEmail = if (isEditingEmail) newEmail else null,
                            newPassword = if (isEditingPassword) newPassword else null,
                            onSuccess = {
                                Toast.makeText(context, "Credenciais atualizadas com sucesso!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onError = {
                                Toast.makeText(context, "Erro ao atualizar: $it", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canSave) Color(0xFF3366FF) else Color.Gray
                    ),
                    enabled = canSave
                ) {
                    Text("Salvar alterações", color = Color.White)
                }
            }
        }
    }
}