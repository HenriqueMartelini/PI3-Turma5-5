package com.puc.superid.utils

import android.util.Patterns
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.*

object StringUtils {

    // Valida se o email está em um formato correto
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Cria o hash da senha usando PBKDF2
    fun hashPassword(password: String): String {
        val salt = generateSalt()  // Gerando um salt único
        val iterations = 10000
        val keyLength = 256
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), iterations, keyLength)
        val hash = secretKeyFactory.generateSecret(spec).encoded
        return salt + ":" + hash.joinToString("") { String.format("%02x", it) }
    }

    // Função para gerar um salt único
    private fun generateSalt(): String {
        return UUID.randomUUID().toString()
    }
}