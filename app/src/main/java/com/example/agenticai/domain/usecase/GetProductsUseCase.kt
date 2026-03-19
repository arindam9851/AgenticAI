package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.Product
import com.example.agenticai.domain.repository.ProductRepository
import javax.inject.Inject
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): Result<List<Product>> =
        repository.getAllProducts()
}