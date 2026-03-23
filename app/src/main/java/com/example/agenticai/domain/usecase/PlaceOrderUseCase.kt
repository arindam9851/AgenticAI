package com.example.agenticai.domain.usecase
import com.example.agenticai.domain.model.CartItem
import com.example.agenticai.domain.repository.CartRepository
import javax.inject.Inject
class PlaceOrderUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(items: List<CartItem>): Result<String> =
        repository.placeOrder(items)
}