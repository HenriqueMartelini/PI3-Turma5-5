package com.puc.superid.data.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object LoginDataSource {
    private const val LOGIN_REQUESTS = "loginRequests"
    private const val LOGIN_PARTNER = "loginPartner"
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    suspend fun authenticateQrCodeLogin(encodedLoginData: String, context: Context): Boolean {
        return try {
            val loginDataJson = String(android.util.Base64.decode(encodedLoginData, android.util.Base64.DEFAULT))
            val loginParts = org.json.JSONObject(loginDataJson)
            val email = loginParts.getString("email")
            val password = loginParts.getString("password")

            val loginPartnerDoc = db.collection(LOGIN_PARTNER)
                .whereEqualTo("email", email)
                .whereEqualTo("senha", password)
                .get()
                .await()

            if (loginPartnerDoc.isEmpty) {
                Log.e("QRLogin", "Login não encontrado na coleção loginPartner.")
                return false
            }

            // Atualiza documento loginRequests para status autenticado
            val updateData = mapOf(
                "status" to "authenticated",
                "email" to email,
                "password" to password,
                "authenticatedAt" to System.currentTimeMillis(),
                "deviceId" to getDeviceId(context)
            )

            val pendingDocs = db.collection(LOGIN_REQUESTS)
                .whereEqualTo("email", email)
                .whereEqualTo("status", "pending")
                .get()
                .await()

            if (pendingDocs.isEmpty) {
                Log.e("QRLogin", "Documento loginRequest pendente não encontrado para o email.")
                return false
            }

            val docId = pendingDocs.documents.first().id

            db.collection(LOGIN_REQUESTS)
                .document(docId)
                .set(updateData, SetOptions.merge())
                .await()

            Log.d("QRLogin", "QR Code autenticado com sucesso para email: $email")
            true
        } catch (e: Exception) {
            Log.e("QRLogin", "Erro ao autenticar via QR Code", e)
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