package com.puc.superid.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puc.superid.utils.FirebaseUtils
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    fun createAccount(name: String, email: String, password: String, imei: String, context: Context, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                FirebaseUtils.registerUserInFirestore(name, email, password, imei, context)
                onSuccess()  // Chama callback de sucesso após registro
            } catch (e: Exception) {
                onFailure(e.message ?: "Erro desconhecido")  // Chama callback de erro em caso de falha
            }
        }
    }
}