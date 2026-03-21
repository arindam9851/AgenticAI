package com.example.agenticai.presentation.order

import com.example.agenticai.domain.model.Order

data class OrderState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean  = false,
    val error: String?      = null
) {
    val isEmpty: Boolean get() = orders.isEmpty()
}

sealed class OrderIntent {
    object Refresh : OrderIntent()
}