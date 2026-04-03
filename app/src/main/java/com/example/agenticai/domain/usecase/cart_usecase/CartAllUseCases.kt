package com.example.agenticai.domain.usecase.cart_usecase

data class CartAllUseCases(
    val getCart: GetCartUseCase,
    val addToCart: AddToCartUseCase,
    val removeFromCart: RemoveFromCartUseCase,
    val updateQty: UpdateCartQtyUseCase,
    val clearCart: ClearCartUseCase,
    val placeOrder: PlaceOrderUseCase,
)
