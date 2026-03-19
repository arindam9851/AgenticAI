package com.example.agenticai.core.di


import com.example.agenticai.data.repository.ConfigRepositoryImpl
import com.example.agenticai.data.repository.ProductRepositoryImpl
import com.example.agenticai.domain.repository.ConfigRepository
import com.example.agenticai.domain.repository.ProductRepository
import com.example.agenticai.domain.usecase.*
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideRemoteConfig(): FirebaseRemoteConfig = Firebase.remoteConfig

    @Provides
    @Singleton
    fun provideAgenticAIUseCases(
        getProducts: GetProductsUseCase,
        searchProducts: SearchProductsUseCase,
        filterByPrice: FilterByPriceUseCase,
        filterByCategory: FilterByCategoryUseCase,
        filterByStock: FilterByStockUseCase,
        sortByRating: SortByRatingUseCase,
        sortByPrice: SortByPriceUseCase,
        compareProducts: CompareProductsUseCase,
        getCategories: GetCategoriesUseCase
    ): AgenticAIUseCases =
        AgenticAIUseCases(
            getProducts,
            searchProducts,
            filterByPrice,
            filterByCategory,
            filterByStock,
            sortByRating,
            sortByPrice,
            compareProducts,
            getCategories
        )
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindConfigRepository(
        impl: ConfigRepositoryImpl
    ): ConfigRepository
}