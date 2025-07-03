package com.example.healtwave.data.remote

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {

    @Headers("Content-Type: application/json")
    @POST("api/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @Headers("Content-Type: application/json")
    @POST("api/register/")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse
}