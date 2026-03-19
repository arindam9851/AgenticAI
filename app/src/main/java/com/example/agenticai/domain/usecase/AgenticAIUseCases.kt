package com.example.agenticai.domain.usecase

import jakarta.inject.Inject

data class AgenticAIUseCases@Inject constructor(
    val getProducts: GetProductsUseCase,
    val searchProducts: SearchProductsUseCase,
    val filterByPrice: FilterByPriceUseCase,
    val filterByCategory: FilterByCategoryUseCase,
    val filterByStock: FilterByStockUseCase,
    val sortByRating: SortByRatingUseCase,
    val sortByPrice: SortByPriceUseCase,
    val compareProducts: CompareProductsUseCase,
    val getCategories: GetCategoriesUseCase
)
