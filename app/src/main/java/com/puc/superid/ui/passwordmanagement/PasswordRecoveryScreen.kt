package com.puc.superid.ui.passwordmanagement

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.puc.superid.viewmodel.PasswordRecoveryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


/**
 * Tela para recuperação de senha via e-mail.
 *
 * Exibe um campo para o usuário digitar seu e-mail, um botão para enviar o link de recuperação,
 * e realiza a interação com o [PasswordRecoveryViewModel].
 *
 * @param viewModel ViewModel que gerencia o estado e lógica da recuperação de senha.
 * @param navController Controlador de navegação para gerenciar as transições de telas.
 * @param onLinkSent Callback chamado após o envio bem-sucedido do link de recuperação.
 */


@Composable
fun PasswordRecoveryScreen(
    viewModel: PasswordRecoveryViewModel = viewModel(),
    navController: NavController,
    onLinkSent: () -> Unit
    ) {

    val context = LocalContext.current

    // Observando o estado com collectAsState
    val state by viewModel.uiState.collectAsState()

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
                text = "Recuperar senha",
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
                        value = state.email,
                        onValueChange = { newEmail -> viewModel.onEmailChange(newEmail) },
                        isError = state.emailError,
                        placeholder = { Text("exemplo@gmail.com") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GradientButton(
                        text = "Envia link",
                        gradient = Brush.horizontalGradient(
                            listOf(Color(0xFF2D49FB), Color(0xFF618CFF))
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.sendResetEmail(
                                onSuccess = {
                                    Toast.makeText(context, "Link enviado com sucesso!", Toast.LENGTH_LONG).show()
                                    onLinkSent()
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}


/**
 * Botão estilizado com fundo em gradiente horizontal.
 *
 * @param text Texto exibido no botão.
 * @param gradient Gradiente de cores usado como fundo do botão.
 * @param modifier Modificador do Compose para customização do botão.
 * @param onClick Callback executado quando o botão é clicado.
 */

@Composable
fun GradientButton(
    text: String,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium
        )
    }
}