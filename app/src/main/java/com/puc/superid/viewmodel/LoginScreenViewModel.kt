package com.puc.superid.viewmodel

import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.*

class LoginViewModel : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, emailError = false)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, passwordError = false)
    }

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
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val errorMessage: String? = null
)