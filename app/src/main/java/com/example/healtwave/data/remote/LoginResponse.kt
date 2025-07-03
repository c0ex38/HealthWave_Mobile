package com.example.healtwave.data.remote

data class LoginResponse(
    val message: String,
    val tokens: Tokens,
    val user: User
)

data class Tokens(
    val refresh: String,
    val access: String,
    val expires_in: Double
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val last_login: String
)