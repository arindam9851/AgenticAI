package com.example.agenticai.core.di.usecase

import com.example.agenticai.domain.usecase.splash_usecase.AllSplashUseCases
import com.example.agenticai.domain.usecase.splash_usecase.ObserveSplashPhasesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object SplashUseCaseModule {
    @Provides
    @Singleton
    fun provideSplashUseCases(
        observeSplashPhasesUseCase: ObserveSplashPhasesUseCase,
    ): AllSplashUseCases =
        AllSplashUseCases(
            observeSplashPhases = observeSplashPhasesUseCase,
        )
}