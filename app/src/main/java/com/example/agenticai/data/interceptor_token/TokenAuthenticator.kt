package com.example.agenticai.data.interceptor_token

import com.example.agenticai.domain.repository.AuthApi
import com.example.agenticai.domain.repository.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi
) : Authenticator{
    override fun authenticate(route: Route?, response: Response): Request? {
        // Prevent infinite loop
        if (responseCount(response) >= 2) {
            return null
        }
        val refreshToken = tokenManager.getRefreshToken() ?: return null

        // ⚠️ Blocking call (required here)
        val newToken = runCatching {
            runBlocking {
                authApi.refreshToken(
                    RefreshRequest(refreshToken)
                ).accessToken
            }
        }.getOrNull()

        return newToken?.let { token ->

            tokenManager.saveAccessToken(token)

            // Retry request with new token
            response.request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
    }


}

private fun responseCount(response: Response): Int {
    var count = 1
    var res = response.priorResponse
    while (res != null) {
        count++
        res = res.priorResponse
    }
    return count
}