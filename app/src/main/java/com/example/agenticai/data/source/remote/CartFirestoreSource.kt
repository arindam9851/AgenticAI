package com.example.agenticai.data.source.remote

import com.example.agenticai.data.model.CartItemDto
import com.example.agenticai.domain.model.CartItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartFirestoreSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    // Simple flat collections — no userId needed
    private val cartRef   = db.collection("cart")
    private val ordersRef = db.collection("orders")

    suspend fun getCartItems(): List<Pair<String, CartItemDto>> {
        val snapshot = cartRef.get().await()
        return snapshot.documents.mapNotNull { doc ->
            val dto = doc.toObject(CartItemDto::class.java)
            if (dto != null) Pair(doc.id, dto) else null
        }
    }

    suspend fun addItem(item: CartItem) {
        val docId    = item.name.lowercase().replace(" ", "_")
        val existing = cartRef.document(docId).get().await()
        if (existing.exists()) {
            val currentQty = existing.getLong("quantity") ?: 1
            cartRef.document(docId).update("quantity", currentQty + 1).await()
        } else {
            cartRef.document(docId).set(mapOf(
                "name"     to item.name,
                "category" to item.category,
                "price"    to item.price,
                "imageUrl" to item.imageUrl,
                "quantity" to 1
            )).await()
        }
    }

    suspend fun removeItem(itemId: String) {
        cartRef.document(itemId).delete().await()
    }

    suspend fun updateQuantity(itemId: String, quantity: Int) {
        cartRef.document(itemId).update("quantity", quantity).await()
    }

    suspend fun clearCart() {
        cartRef.get().await().documents.forEach { it.reference.delete().await() }
    }

    suspend fun placeOrder(items: List<CartItem>): String {
        val total    = items.sumOf { it.price * it.quantity }
        val orderRef = ordersRef.add(mapOf(
            "items"    to items.map { mapOf(
                "name"     to it.name,
                "price"    to it.price,
                "quantity" to it.quantity,
                "total"    to it.price * it.quantity
            )},
            "total"    to total,
            "status"   to "placed",
            "placedAt" to FieldValue.serverTimestamp()
        )).await()
        clearCart()
        return orderRef.id
    }
}