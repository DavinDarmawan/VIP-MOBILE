package com.example.vip_mobile.data.repository

import com.example.vip_mobile.data.model.AuthResult
import com.example.vip_mobile.data.model.LoginRequest
import com.example.vip_mobile.data.model.RegisterRequest
import com.example.vip_mobile.data.remote.FakeAuthService

class AuthRepositoryImpl(
    private val authService: FakeAuthService = FakeAuthService
) : AuthRepository {
    override suspend fun login(request: LoginRequest): AuthResult {
        return authService.login(request)
    }

    override suspend fun register(request: RegisterRequest): AuthResult {
        return authService.register(request)
    }
}

