package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.Product
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor() {
    operator fun invoke(products: List<Product>, keyword: String): List<Product> =
        products.filter {
            it.name.contains(keyword, ignoreCase = true) ||
                    it.description.contains(keyword, ignoreCase = true) ||
                    it.category.contains(keyword, ignoreCase = true)
        }.take(6)
}