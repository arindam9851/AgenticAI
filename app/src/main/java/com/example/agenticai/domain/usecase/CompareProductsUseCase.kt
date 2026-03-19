package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.Product
import javax.inject.Inject

class CompareProductsUseCase @Inject constructor() {
    operator fun invoke(
        products: List<Product>,
        name1: String,
        name2: String
    ): Pair<Product?, Product?> {
        val p1 = products.firstOrNull { it.name.contains(name1, ignoreCase = true) }
        val p2 = products.firstOrNull { it.name.contains(name2, ignoreCase = true) }
        return Pair(p1, p2)
    }
}