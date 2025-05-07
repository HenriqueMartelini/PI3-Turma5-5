package com.puc.superid.utils

import android.content.Context
import com.puc.superid.data.model.User

fun recoverLocalUser(context: Context): User? {
    val prefs = context.getSharedPreferences("usuario", Context.MODE_PRIVATE)
    val email = prefs.getString("email", null)
    val senha = prefs.getString("senha_hash", null)
    return if (email != null && senha != null) {
        User(uid = "", name = "", email = email, password = senha, imei = "")
    } else {
        null
    }
}
