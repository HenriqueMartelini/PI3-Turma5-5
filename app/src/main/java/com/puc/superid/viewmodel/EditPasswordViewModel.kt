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
    fun loadPasswordData(userId: String, category: String, site: String, context: Context) {
        if (userId.isBlank() || category.isBlank() || site.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "IDs inválidos")
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("categories")
            .document(category)
            .collection("logins")
            .document(site)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val username = snapshot.getString("email") ?: ""
                    var password = snapshot.getString("password") ?: ""
                    var isEncrypted = false

                    try {
                        val decrypted = StringUtils.decryptString(context, password)
                        password = decrypted
                        isEncrypted = true
                    } catch (e: Exception) {
                        Log.d("EditPasswordViewModel", "Senha não criptografada ou erro ao descriptografar")
                    }

                    _uiState.value = PasswordUiState(
                        id = site,
                        title = site,
                        username = username,
                        password = password,
                        category = category,
                        isEncrypted = isEncrypted
                    )
                } else {
                    _uiState.value = _uiState.value.copy(error = "Login não encontrado")
                }
            }
            .addOnFailureListener { exception ->
                _uiState.value = _uiState.value.copy(error = exception.message ?: "Erro ao carregar")
            }
    }

    fun updateCredentials(
        userId: String,
        category: String,
        context: Context,
        newEmail: String?,
        newPassword: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val site = uiState.value.id
        if (userId.isBlank() || category.isBlank() || site.isBlank()) {
            onError("IDs inválidos")
            return
        }

        val updates = mutableMapOf<String, Any>()
        newEmail?.let { updates["email"] = it }

        newPassword?.let {
            try {
                updates["password"] = StringUtils.encryptString(context, it)
            } catch (e: Exception) {
                onError("Falha ao criptografar senha")
                return
            }
        }

        db.collection("users")
            .document(userId)
            .collection("categories")
            .document(category)
            .collection("logins")
            .document(site)
            .update(updates)
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(
                    username = newEmail ?: uiState.value.username,
                    password = newPassword ?: uiState.value.password,
                    isEncrypted = newPassword != null || uiState.value.isEncrypted
                )
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onError(exception.localizedMessage ?: "Erro ao atualizar")
            }
    }

    fun deletePassword(
        userId: String,
        category: String,
        site: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (userId.isBlank() || category.isBlank() || site.isBlank()) {
            onError("IDs inválidos")
            return
        }

        Log.d("DeletePassword", "Tentando excluir: users/$userId/categories/$category/logins/$site")

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("categories")
            .document(category)
            .collection("logins")
            .document(site)
            .delete()
            .addOnSuccessListener {
                Log.d("DeletePassword", "Documento excluído com sucesso")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("DeletePassword", "Erro ao excluir: ${exception.message}")
                onError(exception.message ?: "Erro ao excluir")
            }
    }
}