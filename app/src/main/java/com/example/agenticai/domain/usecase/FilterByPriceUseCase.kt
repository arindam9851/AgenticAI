package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.Product
import javax.inject.Inject

class FilterByPriceUseCase @Inject constructor() {
    operator fun invoke(
        products: List<Product>,
        maxPrice: Int,
        category: String = ""
    ): List<Product> =
        products
            .filter { it.price <= maxPrice }
            .filter { category.isEmpty() || it.category.equals(category, ignoreCase = true) }
            .sortedBy { it.price }
            .take(8)
}