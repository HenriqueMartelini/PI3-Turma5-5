package com.puc.superid.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Classe que verifica e solicita permissões relacionadas ao estado do telefone
 */
object PermissionUtils {

    /**
     * Verifica se a permissão para acessar o estado do telefone foi concedida
     *
     * Checa se a permissão para ler o estado do telefone, com o método READ_PHONE_STATE,
     * foi concedida, retornando true se a permissão foi dada e false caso contrário
     *
     * @return true se a permissão foi concedida, caso contrário false
     */
    fun isPhoneStatePermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    /**
     * Solicita a permissão para acessar o estado do telefone
     *
     * Solicita ao usuário a permissão para ler o estado do telefone
     * (READ_PHONE_STATE), caso ainda não tenha sido concedida
     *
     */
    fun requestPhoneStatePermission(context: Context) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            1
        )
    }
}