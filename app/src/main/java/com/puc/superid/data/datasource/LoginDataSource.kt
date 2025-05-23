package com.puc.superid.data.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

object LoginDataSource {
    private const val LOGIN_REQUESTS = "loginRequests"
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    suspend fun authenticateQrCodeLogin(encodedLoginData: String, context: Context): Boolean {
        return try {
            val loginDataJson = String(android.util.Base64.decode(encodedLoginData, android.util.Base64.DEFAULT))
            val loginData = org.json.JSONObject(loginDataJson)

            val token = loginData.getString("token")

            val loginRequestRef = db.collection(LOGIN_REQUESTS).document(token)
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

            val updateData = mapOf(
                "status" to "authenticated",
                "authenticatedAt" to FieldValue.serverTimestamp(),
                "deviceId" to getDeviceId(context),
                "user" to currentUser.uid,
                "userEmail" to currentUser.email
            )

            loginRequestRef.update(updateData).await()

            Log.d("QRLogin", "QR code login autenticado com sucesso")
            true
        } catch (e: Exception) {
            Log.e("QRLogin", "Erro em autenticar o QRCode", e)
            false
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(context: Context): String =
        android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        )
}