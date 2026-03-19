package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.Product
import javax.inject.Inject

class FilterByStockUseCase @Inject constructor() {
    operator fun invoke(
        products: List<Product>,
        inStock: Boolean,
        category: String = ""
    ): List<Product> =
        products
            .filter { it.inStock == inStock }
            .filter { category.isEmpty() || it.category.equals(category, ignoreCase = true) }
            .sortedBy { it.name }
}