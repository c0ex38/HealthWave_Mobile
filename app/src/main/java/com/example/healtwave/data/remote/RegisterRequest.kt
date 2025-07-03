package com.example.healtwave.data.remote

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val password_confirm: String,
    val first_name: String,
    val last_name: String
)