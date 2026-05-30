package com.example.vip_mobile.data.model

data class AuthUser(
    val name: String,
    val phone: String
)

data class LoginRequest(
    val phone: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val phone: String,
    val password: String
)

enum class AuthError {
    INVALID_CREDENTIALS,
    PHONE_ALREADY_REGISTERED
}

sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val error: AuthError) : AuthResult()
}

