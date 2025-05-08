package com.puc.superid.viewmodel

import android.util.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.*

class PasswordRecoveryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecoveryUiState())
    val uiState: StateFlow<RecoveryUiState> = _uiState

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

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

// Classe para o gerenciamento dos dados da recuperação de senha
data class RecoveryUiState(
    val email: String = "",
    val emailError: Boolean = false
)