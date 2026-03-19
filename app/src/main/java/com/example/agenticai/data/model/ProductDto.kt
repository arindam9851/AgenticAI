package com.example.agenticai.data.model

import com.example.agenticai.domain.model.Product

data class ProductDto(
    val name: String = "",
    val category: String = "",
    val price: Int = 0,
    val description: String = "",
    val imageUrl: String = "",
    val inStock: Boolean = true,
    val rating: Double = 0.0
) {
    fun toDomain(id: String) = Product(
        id = id,
        name = name,
        category = category,
        price = price,
        description = description,
        imageUrl = imageUrl,
        inStock = inStock,
        rating = rating
    )
}