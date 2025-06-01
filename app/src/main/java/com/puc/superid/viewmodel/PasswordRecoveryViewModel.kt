package com.puc.superid.viewmodel

import RecoveryUiState
import android.util.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.*

class PasswordRecoveryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState

    /**
     * Atualiza o email no estado da UI.
     *
     * @param newEmail Novo valor do email
     */

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    /**
     * Envia o email de recuperação de senha para o email informado.
     *
     * Valida o formato do email antes de tentar enviar o email de recuperação.
     *
     * @param onSuccess Callback chamado quando o email foi enviado com sucesso
     * @param onError Callback chamado quando ocorre um erro, com mensagem de erro
     */

    fun sendResetEmail(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val email = _uiState.value.email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(emailError = true)
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    val message = task.exception?.localizedMessage ?: "Erro ao enviar e-mail"
                    onError(message)
                }
            }
    }
}