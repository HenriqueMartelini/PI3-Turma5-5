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

/**
 * Objeto responsável por lidar com autenticação de login via QR Code.
 *
 * Esta classe fornece métodos para autenticar o login de usuários escaneando um QR Code
 * gerado pelo site parceiro, atualizando o status da requisição de login no Firestore.
 */
object LoginDataSource {
    private const val LOGIN_REQUESTS = "loginRequests"
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    /**
     * Autentica uma solicitação de login usando um token de QR Code.
     *
     * - Verifica se o token está presente e válido.
     * - Busca a requisição de login correspondente no Firestore.
     * - Checa se a requisição expirou ou já foi autenticada.
     * - Caso válido, atualiza o status para "authenticated", registra o deviceId
     *   e o usuário autenticado.
     *
     * @param loginToken Token recebido após escanear o QR Code
     * @param context Context necessário para obter o IMEI do dispositivo
     * @return true se a autenticação for bem-sucedida, false caso contrário
     */

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