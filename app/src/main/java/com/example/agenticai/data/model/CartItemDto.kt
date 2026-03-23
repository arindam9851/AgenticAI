package com.example.agenticai.data.model

import com.example.agenticai.domain.model.CartItem

data class CartItemDto(
    val name: String = "",
    val category: String = "",
    val price: Int = 0,
    val imageUrl: String = "",
    val quantity: Int = 1
) {
    fun toDomain(id: String) = CartItem(
        id       = id,
        name     = name,
        category = category,
        price    = price,
        imageUrl = imageUrl,
        quantity = quantity
    )
}

fun CartItem.toDto() = CartItemDto(
    name     = name,
    category = category,
    price    = price,
    imageUrl = imageUrl,
    quantity = quantity
)
