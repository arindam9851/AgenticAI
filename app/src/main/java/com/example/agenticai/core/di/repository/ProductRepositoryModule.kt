package com.example.agenticai.core.di.repository

import com.example.agenticai.data.repository.ConfigRepositoryImpl
import com.example.agenticai.data.repository.ProductRepositoryImpl
import com.example.agenticai.domain.repository.ConfigRepository
import com.example.agenticai.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class ProductRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl,
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindConfigRepository(
        impl: ConfigRepositoryImpl,
    ): ConfigRepository
}