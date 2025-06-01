package com.puc.superid.ui.passwordmanagement

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.utils.StringUtils.isValidEmail
import com.puc.superid.viewmodel.EditPasswordViewModel


/**
 * Activity que exibe a tela para editar login (email) e senha de um site/categoria específico do usuário.
 * Recebe via Intent os parâmetros necessários para carregar os dados e iniciar a edição.
 */

class EditPasswordActivity : ComponentActivity() {
    companion object {
        const val EXTRA_USER_ID = "user_id"         /** Chave para obter o ID do usuário da Intent */
        const val EXTRA_CATEGORY = "category"       /** Chave para obter a categoria da Intent */
        const val EXTRA_SITE = "site"               /** Chave para obter o nome do site da Intent */
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""
        val category = intent.getStringExtra(EXTRA_CATEGORY) ?: ""
        val site = intent.getStringExtra(EXTRA_SITE) ?: ""

        setContent {
            SuperidTheme {
                EditPasswordScreen(
                    userId = userId,
                    category = category,
                    site = site,
                    onBack = { finish() }
                )
            }
        }
    }
}


/**
 * Composable que mostra a tela de edição do login e senha.
 *
 * @param userId ID do usuário para buscar e atualizar os dados.
 * @param category Categoria do login (ex: "WebSite", "Email").
 * @param site Nome do site ou serviço que o login representa.
 * @param onBack Função executada ao voltar da tela.
 * @param viewModel ViewModel que gerencia os dados e lógica da edição.
 */


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasswordScreen(
    userId: String,
    category: String,
    site: String,
    onBack: () -> Unit,
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

    // Carrega os dados quando a tela é aberta
    LaunchedEffect(userId, category, site) {
        viewModel.loadPasswordData(userId, category, site, context)
    }

    // Preenche os campos de edição quando os dados são carregados
    LaunchedEffect(uiState.username, uiState.password) {
        if (uiState.username.isNotBlank() && newEmail.isEmpty()) {
            newEmail = uiState.username
            confirmNewEmail = uiState.username
        }
    }

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
            !isEditingPassword || (
                    newPassword.isNotBlank() &&
                            confirmNewPassword.isNotBlank() &&
                            newPassword == confirmNewPassword
                    )
        }
    }

    val canSave by remember(isEditingEmail, isEditingPassword, isEmailValid, isPasswordValid) {
        derivedStateOf {
            (isEditingEmail || isEditingPassword) &&
                    (!isEditingEmail || isEmailValid) &&
                    (!isEditingPassword || isPasswordValid)
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0D0B2D), Color(0xFF0033FF))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Login", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.Done,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabeçalho
                Text(
                    text = site,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Categoria: $category",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Card com informações atuais
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Email atual
                        Text("Email atual:", color = Color.White.copy(alpha = 0.8f))
                        Text(
                            text = uiState.username.ifEmpty { "Carregando..." },
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Senha atual
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Senha atual:", color = Color.White.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Done else Icons.Filled.Lock,
                                    contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                                    tint = Color.White
                                )
                            }
                        }
                        Text(
                            text = if (passwordVisible && uiState.password.isNotBlank()) uiState.password else "••••••",
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Formulário de edição
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Edição de email
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isEditingEmail,
                                onCheckedChange = { isEditingEmail = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Cyan,
                                    uncheckedColor = Color.White.copy(alpha = 0.8f)
                                )
                            )
                            Text("Editar email", color = Color.White)
                        }

                        if (isEditingEmail) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newEmail,
                                onValueChange = { newEmail = it },
                                label = { Text("Novo email", color = Color.White.copy(alpha = 0.8f)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Color.Cyan,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f)
                                ),
                                isError = newEmail.isNotBlank() && !isValidEmail(newEmail)
                            )

                            if (newEmail.isNotBlank() && !isValidEmail(newEmail)) {
                                Text(
                                    text = "Formato de email inválido",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = confirmNewEmail,
                                onValueChange = { confirmNewEmail = it },
                                label = { Text("Confirmar email", color = Color.White.copy(alpha = 0.8f)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Color.Cyan,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f)
                                ),
                                isError = newEmail.isNotBlank() && confirmNewEmail.isNotBlank() && newEmail != confirmNewEmail
                            )

                            if (newEmail.isNotBlank() && confirmNewEmail.isNotBlank() && newEmail != confirmNewEmail) {
                                Text(
                                    text = "Os emails não coincidem",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Edição de senha
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = isEditingPassword,
                                onCheckedChange = { isEditingPassword = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.Cyan,
                                    uncheckedColor = Color.White.copy(alpha = 0.8f)
                                )
                            )
                            Text("Editar senha", color = Color.White)
                        }

                        if (isEditingPassword) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Nova senha", color = Color.White.copy(alpha = 0.8f)) },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Color.Cyan,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = confirmNewPassword,
                                onValueChange = { confirmNewPassword = it },
                                label = { Text("Confirmar senha", color = Color.White.copy(alpha = 0.8f)) },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Color.Cyan,
                                    unfocusedLabelColor = Color.White.copy(alpha = 0.8f)
                                ),
                                isError = newPassword.isNotBlank() && confirmNewPassword.isNotBlank() && newPassword != confirmNewPassword
                            )

                            if (newPassword.isNotBlank() && confirmNewPassword.isNotBlank() && newPassword != confirmNewPassword) {
                                Text(
                                    text = "As senhas não coincidem",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão de salvar
                Button(
                    onClick = {
                        if (canSave) {
                            viewModel.updateCredentials(
                                userId = userId,
                                category = category,
                                context = context,
                                newEmail = if (isEditingEmail) newEmail else null,
                                newPassword = if (isEditingPassword) newPassword else null,
                                onSuccess = {
                                    Toast.makeText(context, "Credenciais atualizadas!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                },
                                onError = { error ->
                                    Toast.makeText(context, "Erro: $error", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canSave) Color(0xFF3366FF) else Color.Gray
                    )
                ) {
                    Text("Salvar Alterações", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botão de excluir
                Button(
                    onClick = {
                        viewModel.deletePassword(
                            userId = userId,
                            category = category,
                            site = site,
                            onSuccess = {
                                Toast.makeText(context, "Login excluído com sucesso", Toast.LENGTH_SHORT).show()
                                onBack()
                            },
                            onError = { error ->
                                Toast.makeText(context, "Erro ao excluir: $error", Toast.LENGTH_LONG).show()
                                Log.e("EditPassword", "Erro ao excluir: $error")
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.7f)
                    )
                ) {
                    Text("Excluir Login", color = Color.White)
                }
            }
        }
    }
}