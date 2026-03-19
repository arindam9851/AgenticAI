package com.example.agenticai.domain.usecase
import com.example.agenticai.domain.model.Product
import javax.inject.Inject
class SortByRatingUseCase @Inject constructor() {
    operator fun invoke(
        products: List<Product>,
        category: String = "",
        ascending: Boolean = false  // false = highest first, true = lowest first
    ): List<Product> =
        products
            .filter { category.isEmpty() || it.category.equals(category, ignoreCase = true) }
            .let { list ->
                if (ascending) list.sortedBy { it.rating }
                else list.sortedByDescending { it.rating }
            }
            .take(6)
}