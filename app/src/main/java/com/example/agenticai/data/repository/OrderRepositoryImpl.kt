package com.example.agenticai.data.repository

import com.example.agenticai.data.source.remote.OrderFirestoreSource
import com.example.agenticai.domain.model.Order
import com.example.agenticai.domain.repository.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val source: OrderFirestoreSource
) : OrderRepository {
    override suspend fun getOrders(): Result<List<Order>> =
        runCatching { source.getOrders() }
}