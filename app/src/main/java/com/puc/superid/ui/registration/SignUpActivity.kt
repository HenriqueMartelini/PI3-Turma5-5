package com.puc.superid.ui.registration

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.puc.superid.ui.theme.SuperidTheme
import com.puc.superid.utils.DeviceUtils
import com.puc.superid.utils.StringUtils
import com.puc.superid.viewmodel.SignUpViewModel
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.puc.superid.R

/**
 * Activity responsável pela tela de cadastro de usuário no aplicativo
 * Utiliza o Jetpack Compose para criar a interface gráfica e o [SignUpViewModel] para gerenciar a lógica
 * de criação de conta e comunicação com o Firebase
 */
class SignUpActivity : ComponentActivity() {

    private val solwayFamily = FontFamily(
        Font(R.font.solway_bold, FontWeight.Bold),
        Font(R.font.solway_regular, FontWeight.Normal),
        Font(R.font.solway_medium, FontWeight.Medium),
        Font(R.font.solway_light, FontWeight.Light),
        Font(R.font.solway_extrabold, FontWeight.ExtraBold)
    )

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

    @Preview(showBackground = true)
    @Composable
    fun SignUpScreenPreview() {
        SuperidTheme {
            SignUpScreen()
        }
    }

    @Composable
    fun SignUpScreen() {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var isNameFocused by remember { mutableStateOf(false) }
        var isEmailFocused by remember { mutableStateOf(false) }
        var isPasswordFocused by remember { mutableStateOf(false) }

        // Contexto da aplicação, necessário para obter o IMEI
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current


        fun unfocused(){
            focusManager.clearFocus()
        }

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
                    name,
                    trimmedEmail,
                    trimmedPassword,
                    imei,
                    context,
                    onSuccess = {
                        message =
                            "Cadastro realizado com sucesso! Verifique seu e-mail para validar sua conta."
                    },
                    onFailure = { errorMessage ->
                        message = errorMessage
                    }
                )
            } else {
                message = "Preencha todos os campos!"
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF020024), Color(0xFF090979))))
                .padding(vertical = 30.dp)
                .clickable { unfocused() },
            contentAlignment = Alignment.TopStart,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Criar conta",
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp,
                        fontFamily = solwayFamily
                    ),
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xAA4B5C8A))
                ) {
                    Box {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(24.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                val offset = Offset(5.0f, 10.0f)
                                Text(
                                    text = "Nome",
                                    style = TextStyle(
                                        color = Color.Cyan,
                                        fontSize = 24.sp,
                                        shadow = Shadow(
                                            color = Color.Black,
                                            offset = offset,
                                            blurRadius = 8f
                                        )
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 35.dp, top = 2.dp, bottom = 12.dp)
                                )
                            }
                            TextField(
                                value = name,
                                onValueChange = { name = it },
                                label = {
                                    if (name.isEmpty() && !isNameFocused) {
                                        Text("John Doe", color = Color.Gray)
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(0xFFCECECE),
                                    focusedContainerColor = Color.White
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .onFocusChanged { focusState ->
                                        isNameFocused = focusState.isFocused
                                    }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                val offset = Offset(5.0f, 10.0f)
                                Text(
                                    text = "Email",
                                    style = TextStyle(
                                        color = Color.Cyan,
                                        fontSize = 24.sp,
                                        shadow = Shadow(
                                            color = Color.Black,
                                            offset = offset,
                                            blurRadius = 8f
                                        )
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 35.dp, top = 2.dp, bottom = 12.dp)
                                )
                            }
                            TextField(
                                value = email,
                                onValueChange = { email = it },
                                label = {
                                    if (email.isEmpty() && !isEmailFocused) {
                                        Text("johndoe@mgail.com", color = Color.Gray)
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(0xFFCECECE),
                                    focusedContainerColor = Color.White
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .onFocusChanged { focusState ->
                                        isEmailFocused = focusState.isFocused
                                    }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(modifier = Modifier.fillMaxWidth()) {
                                val offset = Offset(5.0f, 10.0f)
                                Text(
                                    text = "Senha",
                                    style = TextStyle(
                                        color = Color.Cyan,
                                        fontSize = 24.sp,
                                        shadow = Shadow(
                                            color = Color.Black,
                                            offset = offset,
                                            blurRadius = 8f
                                        )
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 35.dp, top = 2.dp, bottom = 12.dp)
                                )
                            }
                            TextField(
                                value = password,
                                onValueChange = { password = it },
                                label = {
                                    if (password.isEmpty() && !isPasswordFocused) {
                                        Text("password123", color = Color.Gray)
                                    }
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                colors = TextFieldDefaults.colors(
                                    unfocusedContainerColor = Color(0xFFCECECE),
                                    focusedContainerColor = Color.White
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .onFocusChanged { focusState ->
                                        isPasswordFocused = focusState.isFocused
                                    }
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { createAccount() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF021A4C),
                                                Color(0xFF045DDD)
                                            )
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    ),
                                shape = RoundedCornerShape(24.dp),
                                contentPadding = PaddingValues()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF021A4C),
                                                    Color(0xFF045DDD)
                                                )
                                            ),
                                            shape = RoundedCornerShape(24.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Criar conta",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }

                            if (message.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = message,
                                    color = if (message.contains("sucesso")) Color.Green else Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}