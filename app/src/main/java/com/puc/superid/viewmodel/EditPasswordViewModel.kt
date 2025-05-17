package com.puc.superid.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PasswordUiState(
    val id: String = "",
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val category: String = ""
)

class EditPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState

    private val db = FirebaseFirestore.getInstance()

    fun loadPasswordData(documentId: String) {
        val documentRef = db.collection("loginPartners").document(documentId)

        documentRef.get()
            .addOnSuccessListener { snapshot ->
                snapshot?.let {
                    val username = it.getString("login") ?: ""
                    val password = it.getString("senha") ?: ""
                    println("Carregado do Firestore: email = $username, senha = $password")
                    _uiState.value = PasswordUiState(
                        id = documentId,
                        title = it.getString("title") ?: "",
                        username = username,
                        password = password,
                        category = it.getString("categoria") ?: ""
                    )
                }
            }
    }

    fun onFieldChange(title: String, username: String, password: String, category: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            username = username,
            password = password,
            category = category
        )
    }

    fun updatePassword(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val data = uiState.value
        if (data.id.isNotEmpty()) {
            val updates = mapOf(
                "login" to data.username,
                "senha" to data.password,
                "categoria" to data.category
            )

            db.collection("loginPartners").document(data.id)
                .update(updates)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Erro ao atualizar") }
        }
    }

    fun deletePassword(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val data = uiState.value
        if (data.id.isNotEmpty()) {
            db.collection("loginPartners").document(data.id)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Erro ao excluir") }
        }
    }
}