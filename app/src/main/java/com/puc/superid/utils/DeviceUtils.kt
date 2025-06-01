package com.puc.superid.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log

/**
 * Classe responsável por fornecer métodos relacionados a dispositivos
 * Como obter o IMEI
 */
object DeviceUtils {

    /**
     * Obtém o IMEI ou o identificador único do dispositivo
     * Retorna o ID do dispositivo usando a configuração do Android
     * Em caso de erro, ele retorna mensagens específicas para cada situação
     *
     * @param context Contexto da aplicação
     */
    @SuppressLint("HardwareIds")
    fun getIMEI(context: Context): String {
        return try {
            // Retorna o ID do dispositivo
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: SecurityException) {
            // Caso não tenha permissões para acessar o ID
            Log.e("DeviceUtils", "Erro ao obter IMEI: ${e.message}")
            "PERMISSION_DENIED"
        } catch (e: Exception) {
            // Caso ocorra algum outro erro inesperado
            Log.e("DeviceUtils", "Erro inesperado ao obter IMEI: ${e.message}")
            "UNKNOWN"
        }
    }
}