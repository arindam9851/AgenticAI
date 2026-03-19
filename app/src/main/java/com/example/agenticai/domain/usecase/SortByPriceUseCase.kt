package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.Product
import javax.inject.Inject

class SortByPriceUseCase @Inject constructor() {
    operator fun invoke(
        products: List<Product>,
        category: String = "",
        ascending: Boolean = true  // true = cheapest first, false = most expensive first
    ): List<Product> =
        products
            .filter { category.isEmpty() || it.category.equals(category, ignoreCase = true) }
            .let { list ->
                if (ascending) list.sortedBy { it.price }
                else list.sortedByDescending { it.price }
            }
            .take(6)
}