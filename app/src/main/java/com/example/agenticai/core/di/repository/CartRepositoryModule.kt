package com.example.agenticai.core.di.repository

import com.example.agenticai.data.repository.CartRepositoryImpl
import com.example.agenticai.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class CartRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl,
    ): CartRepository
}