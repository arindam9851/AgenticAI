package com.example.agenticai.domain.usecase.splash_usecase

import com.example.agenticai.presentation.splash.SplashContract
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObserveSplashPhasesUseCase @Inject constructor(){
    operator fun invoke(): Flow<SplashContract.AnimationPhase> = flow {
        emit(SplashContract.AnimationPhase.PARTICLES); delay(400)
        emit(SplashContract.AnimationPhase.LOGO);      delay(500)
        emit(SplashContract.AnimationPhase.TEXT);      delay(400)
        emit(SplashContract.AnimationPhase.PULSE);     delay(900)
        emit(SplashContract.AnimationPhase.DONE)
    }
}