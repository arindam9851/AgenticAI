package com.example.agenticai.data.api_service

import okhttp3.Response
import retrofit2.http.GET

interface ApiService {
    @GET("users/1")
    suspend fun getUser(): Response

    @GET("posts/1")
    suspend fun getPost(): Response
}