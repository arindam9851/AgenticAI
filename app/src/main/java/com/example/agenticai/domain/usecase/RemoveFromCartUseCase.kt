package com.example.agenticai.domain.usecase
import com.example.agenticai.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(itemId: String): Result<Unit> =
        repository.removeItem(itemId)
}