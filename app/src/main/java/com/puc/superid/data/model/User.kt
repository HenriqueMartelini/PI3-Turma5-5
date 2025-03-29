package com.puc.superid.data.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val imei: String,
    val password: String? = null
)