package com.example.agenticai.data.source.remote

import com.example.agenticai.domain.model.Order
import com.example.agenticai.domain.model.OrderItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderFirestoreSource @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val ordersRef = db.collection("orders")

    suspend fun getOrders(): List<Order> {
        val snapshot = ordersRef
            .orderBy("placedAt", Query.Direction.DESCENDING)
            .get().await()

        return snapshot.documents.mapNotNull { doc ->
            try {
                val itemsRaw = doc.get("items") as? List<Map<String, Any>> ?: emptyList()
                val items    = itemsRaw.map { map ->
                    OrderItem(
                        name     = map["name"] as? String ?: "",
                        price    = (map["price"] as? Long)?.toInt() ?: 0,
                        quantity = (map["quantity"] as? Long)?.toInt() ?: 1,
                        total    = (map["total"] as? Long)?.toInt() ?: 0
                    )
                }
                Order(
                    id       = doc.id,
                    items    = items,
                    total    = (doc.getLong("total") ?: 0).toInt(),
                    status   = doc.getString("status") ?: "placed",
                    placedAt = doc.getTimestamp("placedAt")
                )
            } catch (e: Exception) { null }
        }
    }
}