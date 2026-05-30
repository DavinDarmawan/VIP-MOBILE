package com.example.vip_mobile.data.repository

import com.example.vip_mobile.data.model.AuthResult
import com.example.vip_mobile.data.model.LoginRequest
import com.example.vip_mobile.data.model.RegisterRequest

interface AuthRepository {
    suspend fun login(request: LoginRequest): AuthResult
    suspend fun register(request: RegisterRequest): AuthResult
}

