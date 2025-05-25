package com.puc.superid.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.puc.superid.utils.StringUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PasswordUiState(
    val id: String = "",
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val category: String = "",
    val error: String? = null,
    val isEncrypted: Boolean = false
)

class EditPasswordViewModel : ViewModel() {
    val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState

    private val db = FirebaseFirestore.getInstance()

    /**
     * Carrega os dados da senha, tentando descriptografar se estiver criptografada
     */
    fun loadPasswordData(documentId: String, context: Context) {
        if (documentId.isBlank()) {
            Log.e("EditPasswordViewModel", "Document ID is empty.")
            return
        }

        val documentRef = db.collection("loginPartner").document(documentId)

        documentRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val username = snapshot.getString("email") ?: ""
                    var password = snapshot.getString("senha") ?: ""
                    var isEncrypted = false

                    try {
                        val decrypted = StringUtils.decryptString(context, password)
                        password = decrypted
                        isEncrypted = true
                    } catch (e: Exception) {
                        Log.d("EditPasswordViewModel", "Senha não está criptografada ou erro ao descriptografar: ${e.message}")
                    }

                    Log.i("FirestoreData", "Email: $username, Senha: [PROTEGIDA]")
                    _uiState.value = PasswordUiState(
                        id = documentId,
                        title = snapshot.getString("title") ?: "",
                        username = username,
                        password = password,
                        category = snapshot.getString("categoria") ?: "",
                        isEncrypted = isEncrypted
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

    /**
     * Atualiza as credenciais, criptografando a senha se necessário
     */
    fun updateCredentials(
        context: Context,
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

        newPassword?.let {
            try {
                // Criptografa a nova senha antes de salvar
                val encryptedPassword = StringUtils.encryptString(context, it)
                updates["senha"] = encryptedPassword
            } catch (e: Exception) {
                onError("Falha ao criptografar a senha: ${e.message}")
                return
            }
        }

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
                    password = newPassword ?: uiState.value.password,
                    isEncrypted = newPassword != null || uiState.value.isEncrypted
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

    /**
     * Verifica se a criptografia está disponível no dispositivo
     */
    fun isEncryptionAvailable(context: Context): Boolean {
        return StringUtils.isEncryptionAvailable(context)
    }
}