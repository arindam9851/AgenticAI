package com.example.agenticai.domain.model

data class CartItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Int = 0,
    val imageUrl: String = "",
    val quantity: Int = 1
) {
    val total: Int get() = price * quantity
}
