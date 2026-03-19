package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.Product
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor() {
    operator fun invoke(products: List<Product>): List<String> =
        products.map { it.category }.distinct().sorted()
}