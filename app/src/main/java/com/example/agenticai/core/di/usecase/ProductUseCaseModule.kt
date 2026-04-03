package com.example.agenticai.core.di.usecase

import com.example.agenticai.domain.usecase.agentic_ai_usecase.AgenticAIUseCases
import com.example.agenticai.domain.usecase.agentic_ai_usecase.CompareProductsUseCase
import com.example.agenticai.domain.usecase.agentic_ai_usecase.FilterByCategoryUseCase
import com.example.agenticai.domain.usecase.agentic_ai_usecase.FilterByPriceUseCase
import com.example.agenticai.domain.usecase.agentic_ai_usecase.FilterByStockUseCase
import com.example.agenticai.domain.usecase.agentic_ai_usecase.GetCategoriesUseCase
import com.example.agenticai.domain.usecase.agentic_ai_usecase.GetProductsUseCase
import com.example.agenticai.domain.usecase.agentic_ai_usecase.SearchProductsUseCase
import com.example.agenticai.domain.usecase.agentic_ai_usecase.SortByPriceUseCase
import com.example.agenticai.domain.usecase.agentic_ai_usecase.SortByRatingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object ProductUseCaseModule {
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
        getCategories: GetCategoriesUseCase,
    ): AgenticAIUseCases =
        AgenticAIUseCases(
            getProducts = getProducts,
            searchProducts = searchProducts,
            filterByPrice = filterByPrice,
            filterByCategory = filterByCategory,
            filterByStock = filterByStock,
            sortByRating = sortByRating,
            sortByPrice = sortByPrice,
            compareProducts = compareProducts,
            getCategories = getCategories,
        )
}