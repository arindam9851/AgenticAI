package com.example.agenticai.domain.model

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Int = 0,
    val description: String = "",
    val imageUrl: String = "",
    val inStock: Boolean = true,
    val rating: Double = 0.0
)