package com.puc.superid.data.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Date

object LoginDataSource {
    private const val LOGIN_REQUESTS = "loginRequests"
    private val db by lazy { FirebaseFirestore.getInstance() }

    suspend fun authenticateQrCodeLogin(encodedLoginData: String, context: Context): Boolean {
        return try {
            // 1. Decodificar os dados do QR Code
            val loginDataJson = String(android.util.Base64.decode(encodedLoginData, android.util.Base64.DEFAULT))
            val loginData = org.json.JSONObject(loginDataJson)

            // 2. Extrair os dados necessários
            val token = loginData.getString("token")
            val partnerEmail = loginData.getString("partnerEmail")

            // 3. Verificar se o token existe e não está expirado
            val loginRequestDoc = db.collection(LOGIN_REQUESTS).document(token).get().await()

            if (!loginRequestDoc.exists()) {
                Log.e("QRLogin", "Token de login não encontrado")
                return false
            }

            val expiresAt = loginRequestDoc.getDate("expiresAt")
            if (expiresAt != null && expiresAt.before(Date())) {
                Log.e("QRLogin", "Token de login expirado")
                return false
            }

            // 4. Verificar se já está autenticado (evitar múltiplas tentativas)
            if (loginRequestDoc.getString("status") == "authenticated") {
                Log.d("QRLogin", "Login já foi autenticado anteriormente")
                return true
            }

            // 5. Atualizar o documento no Firestore marcando como autenticado
            val updateData = mapOf(
                "status" to "authenticated",
                "authenticatedAt" to FieldValue.serverTimestamp(),
                "deviceId" to getDeviceId(context),
                "userEmail" to partnerEmail // Armazenamos o email do parceiro para referência
            )

            db.collection(LOGIN_REQUESTS)
                .document(token)
                .update(updateData)
                .await()

            Log.d("QRLogin", "Autenticação com parceiro realizada com sucesso")
            true
        } catch (e: Exception) {
            Log.e("QRLogin", "Erro geral no processo", e)
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