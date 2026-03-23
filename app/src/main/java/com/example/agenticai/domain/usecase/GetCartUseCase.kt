package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.model.CartItem
import com.example.agenticai.domain.repository.CartRepository
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(): Result<List<CartItem>> =
        repository.getCartItems()
}