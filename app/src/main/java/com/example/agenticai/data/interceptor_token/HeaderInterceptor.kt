package com.example.agenticai.data.interceptor_token

import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val requestBuilder = chain.request().newBuilder()
            .header("Accept", "application/json")
        tokenManager.getAccessToken().let {
            token ->
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }


}
private fun getToken(): String {
    return "sample_token_123"
    // fetch from EncryptedSharedPreferences / DataStore
}