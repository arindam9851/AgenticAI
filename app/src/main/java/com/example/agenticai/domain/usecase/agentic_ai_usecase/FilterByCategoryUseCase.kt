package com.example.agenticai.domain.usecase.agentic_ai_usecase

import com.example.agenticai.domain.model.Product
import javax.inject.Inject

class FilterByCategoryUseCase @Inject constructor() {
    operator fun invoke(products: List<Product>, category: String): List<Product> =
        products.filter { it.category.equals(category, ignoreCase = true) }
}