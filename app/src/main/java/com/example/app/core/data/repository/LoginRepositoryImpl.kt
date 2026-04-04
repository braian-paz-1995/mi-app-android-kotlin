package com.example.app.core.data.repository

import com.example.core.network.ApiClient
import com.example.core.network.AuthApi
import com.example.core.network.LoginRequest

class LoginRepositoryImpl {
    private val api = ApiClient.retrofit.create(AuthApi::class.java)

    suspend fun login(email: String, password: String): Result<String> {
        return runCatching {
            api.login(LoginRequest(email, password)).token
        }
    }
}