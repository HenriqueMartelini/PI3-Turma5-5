package com.puc.superid.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.puc.superid.utils.StringUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


/**
 * Estado da interface de edição de senha.
 *
 * @property id Identificador da senha (normalmente o site)
 * @property title Título da senha (nome do site ou serviço)
 * @property username Nome de usuário ou email associado à senha
 * @property password Senha armazenada, descriptografada se possível
 * @property category Categoria à qual a senha pertence
 * @property error Mensagem de erro, se houver
 * @property isEncrypted Indica se a senha estava criptografada no banco de dados
 */

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
     * Carrega os dados da senha do Firestore e tenta descriptografar a senha.
     *
     * Caso a senha esteja criptografada, será feita a tentativa de descriptografia.
     * Se não estiver, a senha será usada como está.
     *
     * Atualiza o estado da UI com os dados carregados ou mensagem de erro.
     *
     * @param userId ID do usuário no Firestore
     * @param category Categoria onde a senha está salva
     * @param site Identificador único da senha (site)
     * @param context Contexto Android necessário para a descriptografia
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

    /**
     * Atualiza o email e/ou senha de uma credencial armazenada no Firestore.
     *
     * Se a nova senha for informada, ela será criptografada antes de salvar.
     *
     * Atualiza o estado da UI após sucesso e dispara callbacks de sucesso ou erro.
     *
     * @param userId ID do usuário no Firestore
     * @param category Categoria da senha
     * @param context Contexto Android necessário para criptografia
     * @param newEmail Novo email ou username (opcional)
     * @param newPassword Nova senha (opcional)
     * @param onSuccess Callback chamado em caso de sucesso
     * @param onError Callback chamado em caso de erro, com mensagem de erro
     */

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

    /**
     * Exclui uma senha (documento) do Firestore.
     *
     * Dispara callbacks para informar sucesso ou erro na exclusão.
     *
     * @param userId ID do usuário no Firestore
     * @param category Categoria onde a senha está salva
     * @param site Identificador único da senha (site)
     * @param onSuccess Callback chamado em caso de exclusão bem-sucedida
     * @param onError Callback chamado em caso de erro na exclusão, com mensagem de erro
     */

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