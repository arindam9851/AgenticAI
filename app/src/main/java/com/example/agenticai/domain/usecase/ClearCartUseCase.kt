package com.example.agenticai.domain.usecase
import com.example.agenticai.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        repository.clearCart()
}