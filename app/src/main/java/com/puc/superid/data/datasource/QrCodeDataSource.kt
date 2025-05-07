package com.puc.superid.data.datasource

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.puc.superid.utils.recoverLocalUser
import kotlinx.coroutines.tasks.await

suspend fun confirmarLoginViaQRCode(token: String, context: Context) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    try {
        val currentUser = recoverLocalUser(context)  // Recuperando o usuário local
        if (currentUser == null) {
            Log.e("QRLogin", "Usuário não encontrado localmente.")
            return
        }

        // Realizando o login automático com Firebase Auth
        currentUser.password?.let {
            auth.signInWithEmailAndPassword(currentUser.email, it)
                .addOnSuccessListener {
                    Log.d("QRLogin", "Login realizado com sucesso com o Firebase Auth.")

                    // Agora que o login foi feito com sucesso, podemos confirmar o login no Firestore
                    val loginDoc = firestore.collection("login").document(token)
                    val updateData = mapOf(
                        "authed" to true,
                        "email" to currentUser.email,
                        "passwordHash" to currentUser.password
                    )

                    // Atualizando o Firestore com as credenciais do usuário
                    firestore.collection("login").document(token).update(updateData)

                    Log.d("QRLogin", "Login confirmado no Firestore com sucesso.")
                }
                .addOnFailureListener { exception ->
                    Log.e("QRLogin", "Erro ao realizar o login com o Firebase Auth: ${exception.message}")
                }
        }

    } catch (e: Exception) {
        Log.e("QRLogin", "Erro ao confirmar login: ${e.message}")
    }
}