package com.example.agenticai.domain.repository

import com.example.agenticai.domain.model.Product
import com.example.agenticai.domain.model.CartItem
import com.example.agenticai.domain.model.Order

interface ProductRepository {
    suspend fun getAllProducts(): Result<List<Product>>
}

interface ConfigRepository {
    suspend fun getGroqApiKey(): String
}

interface CartRepository {
    suspend fun getCartItems(): Result<List<CartItem>>
    suspend fun addItem(item: CartItem): Result<Unit>
    suspend fun removeItem(itemId: String): Result<Unit>
    suspend fun updateQuantity(itemId: String, quantity: Int): Result<Unit>
    suspend fun clearCart(): Result<Unit>
    suspend fun placeOrder(items: List<CartItem>): Result<String>
}

interface OrderRepository {
    suspend fun getOrders(): Result<List<Order>>
}