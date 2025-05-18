package com.puc.superid.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PasswordUiState(
    val id: String = "",
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val category: String = "",
    val error: String? = null
)

class EditPasswordViewModel : ViewModel() {
    val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState

    private val db = FirebaseFirestore.getInstance()

    fun loadPasswordData(documentId: String) {
        if (documentId.isBlank()) {
            Log.e("EditPasswordViewModel", "Document ID is empty.")
            return
        }

        val documentRef = db.collection("loginPartner").document(documentId)

        documentRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val username = snapshot.getString("email") ?: ""
                    val password = snapshot.getString("senha") ?: ""
                    Log.i("FirestoreData", "Email: $username, Senha: $password")
                    _uiState.value = PasswordUiState(
                        id = documentId,
                        title = snapshot.getString("title") ?: "",
                        username = username,
                        password = password,
                        category = snapshot.getString("categoria") ?: ""
                    )
                } else {
                    Log.w("EditPasswordViewModel", "Documento não encontrado.")
                    _uiState.value = _uiState.value.copy(error = "Documento não encontrado.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Erro ao carregar dados: ${exception.message}")
                _uiState.value = _uiState.value.copy(error = "Erro ao carregar dados.")
            }
    }

    fun updateCredentials(
        newEmail: String?,
        newPassword: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val documentId = uiState.value.id
        if (documentId.isEmpty()) {
            onError("ID do documento está vazio.")
            return
        }

        val updates = mutableMapOf<String, Any>()
        newEmail?.let { updates["email"] = it }
        newPassword?.let { updates["senha"] = it }

        if (updates.isEmpty()) {
            onError("Nenhum campo para atualizar")
            return
        }

        db.collection("loginPartner").document(documentId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("EditPasswordViewModel", "Credenciais atualizadas com sucesso.")
                _uiState.value = _uiState.value.copy(
                    username = newEmail ?: uiState.value.username,
                    password = newPassword ?: uiState.value.password
                )
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("EditPasswordViewModel", "Erro ao atualizar credenciais: ${exception.message}")
                onError(exception.localizedMessage ?: "Erro ao atualizar")
            }
    }

    fun deletePassword(documentId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (documentId.isNotEmpty()) {
            db.collection("loginPartner").document(documentId)
                .delete()
                .addOnSuccessListener {
                    Log.d("EditPasswordViewModel", "Login excluído com sucesso.")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.e("EditPasswordViewModel", "Erro ao excluir credenciais: ${exception.message}")
                    onError(exception.localizedMessage ?: "Erro ao excluir")
                }
        } else {
            Log.e("EditPasswordViewModel", "ID do documento está vazio.")
            onError("ID do documento está vazio.")
        }
    }
}