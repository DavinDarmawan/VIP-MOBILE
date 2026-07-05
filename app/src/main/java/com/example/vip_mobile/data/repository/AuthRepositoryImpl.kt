package com.example.vip_mobile.data.repository

import com.example.vip_mobile.data.model.AuthError
import com.example.vip_mobile.data.model.AuthResult
import com.example.vip_mobile.data.model.AuthUser
import com.example.vip_mobile.data.model.LoginRequest
import com.example.vip_mobile.data.model.RegisterRequest
import com.example.vip_mobile.data.remote.ApiClient
import com.example.vip_mobile.data.remote.AuthApi
import com.example.vip_mobile.data.remote.LoginBody
import com.example.vip_mobile.data.remote.RegisterBody
import retrofit2.HttpException

class AuthRepositoryImpl(
    private val authApi: AuthApi = ApiClient.retrofit.create(AuthApi::class.java)
) : AuthRepository {
    override suspend fun login(request: LoginRequest): AuthResult {
        return try {
            val resp = authApi.login(LoginBody(phone_number = request.phone, password = request.password))
            AuthResult.Success(AuthUser("", request.phone, resp.access_token))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    if (e.code() == 401) AuthResult.Error(AuthError.INVALID_CREDENTIALS)
                    else AuthResult.Error(AuthError.INVALID_CREDENTIALS)
                }
                else -> AuthResult.Error(AuthError.INVALID_CREDENTIALS)
            }
        }
    }

    override suspend fun register(request: RegisterRequest): AuthResult {
        return try {
            val resp = authApi.register(RegisterBody(full_name = request.name, phone_number = request.phone, password = request.password))
            AuthResult.Success(AuthUser(request.name, request.phone, resp.access_token))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    if (e.code() == 400) AuthResult.Error(AuthError.PHONE_ALREADY_REGISTERED)
                    else AuthResult.Error(AuthError.PHONE_ALREADY_REGISTERED)
                }
                else -> AuthResult.Error(AuthError.PHONE_ALREADY_REGISTERED)
            }
        }
    }
}



