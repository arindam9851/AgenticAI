package com.example.agenticai.domain.repository

import com.example.agenticai.domain.model.Product

interface ProductRepository {
    suspend fun getAllProducts(): Result<List<Product>>
}

interface ConfigRepository {
    suspend fun getGroqApiKey(): String
}