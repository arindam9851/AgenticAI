package com.example.agenticai.domain.model

import com.google.firebase.Timestamp

data class Order(
    val id: String = "",
    val items: List<OrderItem> = emptyList(),
    val total: Int = 0,
    val status: String = "placed",
    val placedAt: Timestamp? = null
)

data class OrderItem(
    val name: String = "",
    val price: Int = 0,
    val quantity: Int = 1,
    val total: Int = 0
)