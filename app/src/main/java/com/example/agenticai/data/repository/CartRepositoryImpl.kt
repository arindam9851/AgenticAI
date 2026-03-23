package com.example.agenticai.data.repository

import com.example.agenticai.data.source.remote.CartFirestoreSource
import com.example.agenticai.domain.model.CartItem
import com.example.agenticai.domain.repository.CartRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val source: CartFirestoreSource
) : CartRepository {

    override suspend fun getCartItems(): Result<List<CartItem>> =
        runCatching {
            source.getCartItems().map { (id, dto) -> dto.toDomain(id) }
        }

    override suspend fun addItem(item: CartItem): Result<Unit> =
        runCatching { source.addItem(item) }

    override suspend fun removeItem(itemId: String): Result<Unit> =
        runCatching { source.removeItem(itemId) }

    override suspend fun updateQuantity(itemId: String, quantity: Int): Result<Unit> =
        runCatching { source.updateQuantity(itemId, quantity) }

    override suspend fun clearCart(): Result<Unit> =
        runCatching { source.clearCart() }

    override suspend fun placeOrder(items: List<CartItem>): Result<String> =
        runCatching { source.placeOrder(items) }
}