package com.puc.superid.viewmodel

import LoginUiState
import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.*

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())

    /**
     * Atualiza o email no estado da UI e limpa o erro de email.
     *
     * @param email Novo valor do email
     */

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, emailError = false)
    }

    /**
     * Atualiza a senha no estado da UI e limpa o erro de senha.
     *
     * @param password Novo valor da senha
     */

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, passwordError = false)
    }

    /**
     * Realiza o login do usuário com email e senha usando FirebaseAuth.
     *
     * Valida o formato do email e o tamanho da senha antes da autenticação.
     * Verifica se o email foi verificado antes de chamar sucesso.
     *
     * @param onSuccess Callback chamado quando o login é bem-sucedido e email verificado
     * @param onFailure Callback chamado em caso de erro, com mensagem de erro
     */

    fun login(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val email = uiState.email.trim()
        val password = uiState.password.trim()

        val validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val validPassword = password.length >= 6

        if (!validEmail || !validPassword) {
            uiState = uiState.copy(
                emailError = !validEmail,
                passwordError = !validPassword
            )
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.isEmailVerified) {
                        onSuccess()
                    } else {
                        onFailure("Verifique seu e-mail antes de fazer login.")
                    }
                } else {
                    val error = task.exception?.localizedMessage ?: "Erro ao fazer login."
                    onFailure(error)
                }
            }
    }


    /**
     * Reenvia o email de verificação para o usuário autenticado.
     *
     * Tenta autenticar o usuário com email e senha e, caso não esteja verificado,
     * envia o email de verificação.
     *
     * @param email Email do usuário
     * @param password Senha do usuário
     * @param onResult Callback que retorna a mensagem de sucesso ou erro
     */

    fun resendVerificationToEmail(email: String, password: String, onResult: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult("Preencha o e-mail e a senha.")
            return
        }

        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && !user.isEmailVerified) {
                        user.sendEmailVerification()
                            .addOnCompleteListener { sendTask ->
                                if (sendTask.isSuccessful) {
                                    onResult("Email de verificação reenviado.")
                                } else {
                                    onResult("Erro ao enviar: ${sendTask.exception?.message}")
                                }
                            }
                    } else {
                        onResult("Email já verificado ou usuário inválido.")
                    }
                } else {
                    onResult("Erro ao autenticar: ${task.exception?.message}")
                }
            }
    }
}