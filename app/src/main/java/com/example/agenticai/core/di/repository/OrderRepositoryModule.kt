package com.example.agenticai.core.di.repository

import com.example.agenticai.data.repository.OrderRepositoryImpl
import com.example.agenticai.domain.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class OrderRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl,
    ): OrderRepository
}