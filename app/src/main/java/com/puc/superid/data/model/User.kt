package com.puc.superid.data.model

/**
 * Molde para criação de usuários
 *
 * @property uid Identificador único do usuário
 * @property name Nome completo do usuário
 * @property email E-mail do usuário
 * @property imei Identificador único do dispositivo
 * @property password Senha do usuário
 */
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val imei: String,
    val password: String,
    val lastLogin: Long? = null
)