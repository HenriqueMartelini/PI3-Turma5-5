package com.puc.superid.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Patterns
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher.DECRYPT_MODE
import javax.crypto.Cipher.ENCRYPT_MODE

/**
 * Classe que gerencia operações com string, como validar se um email é válido, criptografar senhas
 */
object StringUtils {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "SuperID_Key_Alias"
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val IV_LENGTH = 12 // GCM recommended IV length
    private const val TAG_LENGTH = 128 // GCM tag length in bits

    /**
     * Verifica se o e-mail é válido
     *
     * Utiliza a classe Patterns para verificar se o formato do e-mail é válido,
     * retornando true se for um e-mail válido e false caso contrário
     *
     * @param email O endereço de e-mail a ser verificado
     * @return true se o e-mail for válido, caso contrário, false
     */
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Cria um hash para a senha fornecida, utilizando um salt gerado aleatoriamente
     *
     * Este método usa o algoritmo PBKDF2 com HMAC-SHA256 para gerar um hash seguro da senha,
     * com um número de iterações de 10.000 e um comprimento de chave de 256 bits
     * O salt é gerado aleatoriamente e concatenado ao hash final
     *
     * @param password A senha a ser hasheada
     * @return A senha hasheada, composta pelo salt e pelo hash concatenados
     */
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val iterations = 10000
        val keyLength = 256
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), iterations, keyLength)
        val hash = secretKeyFactory.generateSecret(spec).encoded
        return salt + ":" + hash.joinToString("") { String.format("%02x", it) }
    }

    /**
     * Gera um salt aleatório para uso no processo de hashing
     *
     * O salt é gerado utilizando o UUID.randomUUID(), retornando um valor único em formato de string
     *
     * @return O salt gerado aleatoriamente
     */
    private fun generateSalt(): String {
        return UUID.randomUUID().toString()
    }

    fun isValidDomain(domain: String): Boolean {
        val regex = Regex("^www\\.[a-zA-Z0-9.-]+\\.[a-z]{2,}$")
        return regex.matches(domain) && !domain.contains("/")
    }

    // --- Android Keystore Implementation for loginPartner passwords ---

    /**
     * Criptografa uma string usando Android Keystore (para senhas de loginPartner)
     */
    fun encryptString(context: Context, plaintext: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        val secretKey = getOrCreateSecretKey()

        // Gerar IV aleatório
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().nextBytes(iv)

        cipher.init(ENCRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH, iv))
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        // Concatenar IV + ciphertext e codificar em Base64
        val encryptedData = ByteArray(iv.size + ciphertext.size)
        System.arraycopy(iv, 0, encryptedData, 0, iv.size)
        System.arraycopy(ciphertext, 0, encryptedData, iv.size, ciphertext.size)

        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    /**
     * Descriptografa uma string usando Android Keystore (para senhas de loginPartner)
     */
    fun decryptString(context: Context, encryptedString: String): String {
        val encryptedData = Base64.decode(encryptedString, Base64.DEFAULT)
        val cipher = Cipher.getInstance(AES_MODE)
        val secretKey = getOrCreateSecretKey()

        // Extrair IV dos dados criptografados
        val iv = ByteArray(IV_LENGTH)
        System.arraycopy(encryptedData, 0, iv, 0, iv.size)

        // Extrair ciphertext
        val ciphertext = ByteArray(encryptedData.size - iv.size)
        System.arraycopy(encryptedData, iv.size, ciphertext, 0, ciphertext.size)

        cipher.init(DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH, iv))
        val plaintext = cipher.doFinal(ciphertext)

        return String(plaintext, Charsets.UTF_8)
    }

    /**
     * Obtém ou cria uma chave secreta no Android Keystore
     */
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        return if (keyStore.containsAlias(KEY_ALIAS)) {
            (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            createSecretKey()
        }
    }

    /**
     * Cria uma nova chave secreta no Android Keystore
     */
    private fun createSecretKey(): SecretKey {
        return KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        ).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setRandomizedEncryptionRequired(false) // Permitir IV definido pelo usuário
                    .build()
            )
        }.generateKey()
    }

    /**
     * Verifica se a chave de criptografia está disponível
     */
    fun isEncryptionAvailable(context: Context): Boolean {
        return try {
            getOrCreateSecretKey()
            true
        } catch (e: Exception) {
            false
        }
    }
}