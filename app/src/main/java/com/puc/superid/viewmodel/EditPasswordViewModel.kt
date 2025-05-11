package com.puc.superid.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
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
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    fun loadPasswordData(id: String) {
        if (uid != null) {
            db.collection("users").document(uid)
                .collection("passwords").document(id)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null) {
                        _uiState.value = PasswordUiState(
                            id = id,
                            title = doc.getString("title") ?: "",
                            username = doc.getString("username") ?: "",
                            password = doc.getString("password") ?: "",
                            category = doc.getString("category") ?: ""
                        )
                    }
                }
        }
    }

    fun onFieldChange(title: String, username: String, password: String, category: String) {
        _uiState.value = _uiState.value.copy(title = title, username = username, password = password, category = category)
    }

    fun updatePassword(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val data = uiState.value
        if (uid != null && data.id.isNotEmpty()) {
            val updates = mapOf(
                "title" to data.title,
                "username" to data.username,
                "password" to data.password,
                "category" to data.category
            )

            db.collection("users").document(uid)
                .collection("passwords").document(data.id)
                .set(updates)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Erro ao atualizar") }
        }
    }

    fun deletePassword(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val data = uiState.value
        if (uid != null && data.id.isNotEmpty()) {
            db.collection("users").document(uid)
                .collection("passwords").document(data.id)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it.localizedMessage ?: "Erro ao excluir") }
        }
    }
}