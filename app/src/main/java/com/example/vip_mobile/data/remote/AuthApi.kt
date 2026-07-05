package com.example.vip_mobile.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class LoginBody(val phone_number: String, val password: String)
data class RegisterBody(val full_name: String, val phone_number: String, val password: String)
data class AuthResponse(val user_id: Long, val access_token: String)

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginBody): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body body: RegisterBody): AuthResponse
}

