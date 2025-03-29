package com.puc.superid.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi

object DeviceUtils {

    @SuppressLint("HardwareIds")
    fun getIMEI(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: SecurityException) {
            Log.e("DeviceUtils", "Erro ao obter IMEI: ${e.message}")
            "PERMISSION_DENIED"
        } catch (e: Exception) {
            Log.e("DeviceUtils", "Erro inesperado ao obter IMEI: ${e.message}")
            "UNKNOWN"
        }
    }
}