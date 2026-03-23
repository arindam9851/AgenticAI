package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.Order
import com.example.agenticai.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(): Result<List<Order>> =
        repository.getOrders()
}