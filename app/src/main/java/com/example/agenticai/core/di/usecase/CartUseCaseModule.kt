package com.example.agenticai.core.di.usecase

import com.example.agenticai.domain.usecase.cart_usecase.AddToCartUseCase
import com.example.agenticai.domain.usecase.cart_usecase.CartAllUseCases
import com.example.agenticai.domain.usecase.cart_usecase.ClearCartUseCase
import com.example.agenticai.domain.usecase.cart_usecase.GetCartUseCase
import com.example.agenticai.domain.usecase.cart_usecase.PlaceOrderUseCase
import com.example.agenticai.domain.usecase.cart_usecase.RemoveFromCartUseCase
import com.example.agenticai.domain.usecase.cart_usecase.UpdateCartQtyUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartUseCaseModule {
    @Provides
    @Singleton
    fun provideCartUseCases(
        getCartUseCase: GetCartUseCase,
        addToCartUseCase: AddToCartUseCase,
        removeFromCartUseCase: RemoveFromCartUseCase,
        updateCartQtyUseCase: UpdateCartQtyUseCase,
        clearCartUseCase: ClearCartUseCase,
        placeOrderUseCase: PlaceOrderUseCase,
    ): CartAllUseCases =
        CartAllUseCases(
            getCart = getCartUseCase,
            addToCart = addToCartUseCase,
            removeFromCart = removeFromCartUseCase,
            updateQty = updateCartQtyUseCase,
            clearCart = clearCartUseCase,
            placeOrder = placeOrderUseCase,
        )
}