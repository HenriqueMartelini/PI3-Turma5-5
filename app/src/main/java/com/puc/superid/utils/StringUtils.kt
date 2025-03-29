package com.puc.superid.utils

import android.util.Patterns
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.*

object StringUtils {

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val iterations = 10000
        val keyLength = 256
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), iterations, keyLength)
        val hash = secretKeyFactory.generateSecret(spec).encoded
        return salt + ":" + hash.joinToString("") { String.format("%02x", it) }
    }

    private fun generateSalt(): String {
        return UUID.randomUUID().toString()
    }
}