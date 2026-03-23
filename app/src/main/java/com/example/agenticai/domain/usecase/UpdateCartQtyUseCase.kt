package com.example.agenticai.domain.usecase

import com.example.agenticai.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartQtyUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(itemId: String, quantity: Int): Result<Unit> =
        repository.updateQuantity(itemId, quantity)
}