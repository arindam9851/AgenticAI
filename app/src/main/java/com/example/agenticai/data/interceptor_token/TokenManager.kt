package com.example.agenticai.data.interceptor_token

import jakarta.inject.Inject

class TokenManager @Inject constructor(){
    private var accessToken : String = "expire token"
    private var refreshToken : String = "refresh token"

    fun getAccessToken(): String = accessToken
    fun saveAccessToken(token: String) {
        accessToken = token
    }
    fun getRefreshToken(): String = refreshToken
}