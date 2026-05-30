package com.example.vip_mobile.data.remote

import com.example.vip_mobile.data.model.AuthError
import com.example.vip_mobile.data.model.AuthResult
import com.example.vip_mobile.data.model.AuthUser
import com.example.vip_mobile.data.model.LoginRequest
import com.example.vip_mobile.data.model.RegisterRequest
import kotlinx.coroutines.delay

object FakeAuthService {
    private val usersByPhone = mutableMapOf(
        "081234567890" to Pair("vip123", "Nasabah VIP")
    )

    suspend fun login(request: LoginRequest): AuthResult {
        delay(900)
        val user = usersByPhone[request.phone]
        return if (user != null && user.first == request.password) {
            AuthResult.Success(AuthUser(name = user.second, phone = request.phone))
        } else {
            AuthResult.Error(AuthError.INVALID_CREDENTIALS)
        }
    }

    suspend fun register(request: RegisterRequest): AuthResult {
        delay(900)
        if (usersByPhone.containsKey(request.phone)) {
            return AuthResult.Error(AuthError.PHONE_ALREADY_REGISTERED)
        }

        usersByPhone[request.phone] = request.password to request.name
        return AuthResult.Success(AuthUser(name = request.name, phone = request.phone))
    }
}

