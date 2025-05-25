package com.puc.superid.utils

import android.util.Patterns
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.*

/**
 * Classe que gerencia operações com string, como validar se um email é váido, criptograr senhas
 */
object StringUtils {

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
}