package com.puc.superid.ui.registration

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.utils.DeviceUtils
import com.puc.superid.utils.StringUtils
import com.puc.superid.viewmodel.SignUpViewModel

/**
 * Activity responsável pela tela de cadastro de usuário no aplicativo
 * Utiliza o Jetpack Compose para criar a interface gráfica e o [SignUpViewModel] para gerenciar a lógica
 * de criação de conta e comunicação com o Firebase
 */
class SignUpActivity : ComponentActivity() {

    // Instância do FirebaseAuth utilizada para autenticação
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // ViewModel responsável pela lógica de criação de conta
    private lateinit var signUpViewModel: SignUpViewModel

    /**
     * Método chamado quando a Activity é criada. Inicializa o ViewModel e define o conteúdo da tela.
     *
     * @param savedInstanceState Estado salvo da Activity, utilizado para restaurar a UI em caso de reinicialização
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o ViewModel
        signUpViewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)

        // Define o conteúdo da tela utilizando o Jetpack Compose
        setContent {
            SuperidTheme {
                SignUpScreen()
            }
        }
    }

    /**
     * Tela de registro de usuário composta com Jetpack Compose. Contém campos de entrada para nome, e-mail e
     * senha, além de um botão para criar a conta
     *
     * @Composable Função que define a UI da tela de cadastro
     */
    @Composable
    fun SignUpScreen() {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }

        // Contexto da aplicação, necessário para obter o IMEI
        val context = LocalContext.current

        /**
         * Função chamada quando o botão "Criar Conta" é pressionado. Realiza a validação dos campos e chama
         * o ViewModel para criar a conta no Firebase
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun createAccount() {
            // Remove espaços brancos dos campos
            val trimmedName = name.trim()
            val trimmedEmail = email.trim().replace(" ", "")
            val trimmedPassword = password.trim().replace(" ", "")

            // Verifica garantindo que o e-mail ou senha não contém espaços em branco
            if (trimmedEmail.contains(" ") || trimmedPassword.contains(" ")) {
                message = "Email ou senha não podem conter espaços em branco!"
                return
            }

            // Valida os campos antes de prosseguir com a criação da conta
            if (trimmedName.isNotEmpty() && trimmedEmail.isNotEmpty() && trimmedPassword.isNotEmpty()) {
                if (!StringUtils.isValidEmail(trimmedEmail)) {
                    message = "Email inválido!"
                    return
                }

                // Obtém o IMEI do dispositivo
                val imei = DeviceUtils.getIMEI(context)

                // Chama o ViewModel para criar a conta no Firebase
                signUpViewModel.createAccount(
                    name = trimmedName,
                    email = trimmedEmail,
                    password = trimmedPassword,
                    imei = imei,
                    context = context,
                    onSuccess = {
                        message = "Cadastro realizado com sucesso! Verifique seu email para validar sua conta."
                    },
                    onFailure = { errorMessage ->
                        message = errorMessage
                    }
                )
            } else {
                message = "Preencha todos os campos!"
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crie sua conta no SuperID", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(32.dp))

            TextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Senha Mestre") }, visualTransformation = PasswordVisualTransformation())
            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { createAccount() }) {
                Text("Criar Conta")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Exibe a mensagem de erro ou sucesso
            Text(message, color = if (message.startsWith("Erro")) Color.Red else Color.Green)
        }
    }
}