package com.puc.superid.data.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.puc.superid.utils.DeviceUtils
import kotlinx.coroutines.tasks.await
import java.util.Date

object LoginDataSource {
    private const val LOGIN_REQUESTS = "loginRequests"
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    suspend fun authenticateQrCodeLogin(loginToken: String, context: Context): Boolean {
        return try {
            if (loginToken.isBlank()) {
                Log.e("QRLogin", "Token vazio")
                return false
            }

            val loginRequestRef = db.collection(LOGIN_REQUESTS).document(loginToken)
            val loginRequestDoc = loginRequestRef.get().await()

            if (!loginRequestDoc.exists()) {
                Log.e("QRLogin", "Login token não encontrado")
                return false
            }

            val expiresAt = loginRequestDoc.getDate("expiresAt")
            if (expiresAt != null && expiresAt.before(Date())) {
                Log.e("QRLogin", "Login token expirado")
                return false
            }

            if (loginRequestDoc.getString("status") == "authenticated") {
                Log.d("QRLogin", "Login já autenticado")
                return true
            }

            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("QRLogin", "Usuário autenticado não encontrado")
                return false
            }

            val deviceId = DeviceUtils.getIMEI(context)

            val updateData = mapOf(
                "status" to "authenticated",
                "authenticatedAt" to FieldValue.serverTimestamp(),
                "deviceId" to deviceId,
                "user" to currentUser.uid,
                "userEmail" to currentUser.email,
            )

            loginRequestRef.update(updateData).await()

            Log.d("QRLogin", "QR code login autenticado com sucesso")
            true
        } catch (e: Exception) {
            Log.e("QRLogin", "Erro em autenticar o QRCode", e)
            false
        }
    }
}