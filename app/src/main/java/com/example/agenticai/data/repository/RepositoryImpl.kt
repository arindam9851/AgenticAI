package com.example.agenticai.data.repository


import com.example.agenticai.data.source.remote.FirestoreProductSource
import com.example.agenticai.data.source.remote.RemoteConfigSource
import com.example.agenticai.domain.model.Product
import com.example.agenticai.domain.repository.ConfigRepository
import com.example.agenticai.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val source: FirestoreProductSource
) : ProductRepository {

    override suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val products = source.getProducts().map { (id, dto) -> dto.toDomain(id) }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    private val source: RemoteConfigSource
) : ConfigRepository {

    override suspend fun getGroqApiKey(): String {
        source.init()
        return source.getGroqApiKey()
    }
}