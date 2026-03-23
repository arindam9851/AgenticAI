package com.example.agenticai.presentation.cart

import com.example.agenticai.domain.model.CartItem

data class CartState(
    val items: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val isPlacingOrder: Boolean = false,
    val orderPlaced: Boolean = false,
    val orderId: String = "",
    val error: String? = null
) {
    val totalItems: Int get() = items.sumOf { it.quantity }
    val totalPrice: Int get() = items.sumOf { it.total }
    val isEmpty: Boolean get() = items.isEmpty()
}

sealed class CartIntent {
    data class RemoveItem(val itemId: String) : CartIntent()
    data class IncreaseQty(val itemId: String) : CartIntent()
    data class DecreaseQty(val itemId: String) : CartIntent()
    object PlaceOrder : CartIntent()
    object ClearAll : CartIntent()
    object DismissOrderSuccess : CartIntent()
}

sealed class CartEffect {
    object NavigateBack : CartEffect()
    data class ShowMessage(val message: String) : CartEffect()
}