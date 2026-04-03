package com.example.agenticai.core.di.usecase

import com.example.agenticai.domain.usecase.order_usecase.GetOrdersUseCase
import com.example.agenticai.domain.usecase.order_usecase.OrderAllUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object OrderUseCaseModule {
    @Provides
    @Singleton
    fun provideOrderUseCases(
        getOrdersUseCase: GetOrdersUseCase,
    ): OrderAllUseCases =
        OrderAllUseCases(
            getOrders = getOrdersUseCase,
        )

}