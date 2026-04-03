package com.example.agenticai.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenticai.domain.usecase.splash_usecase.AllSplashUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val useCases: AllSplashUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(SplashContract.SplashState())
    val state: StateFlow<SplashContract.SplashState> = _state.asStateFlow()

    private val _effect = Channel<SplashContract.Effect>(Channel.BUFFERED)
    val effect: Flow<SplashContract.Effect> = _effect.receiveAsFlow()

    fun handleIntent(intent: SplashContract.Intent) {
        when (intent) {
            SplashContract.Intent.StartAnimation -> runAnimationSequence()
            SplashContract.Intent.AnimationFinished -> sendNavigateEffect()
        }
    }

    private fun runAnimationSequence() {
        viewModelScope.launch {
            useCases.observeSplashPhases().collect { phase ->
                phase(phase)
            }
            handleIntent(SplashContract.Intent.AnimationFinished)
        }
    }

    private fun sendNavigateEffect() {
        viewModelScope.launch {
            delay(2000)
            _effect.send(SplashContract.Effect.NavigateToChat)
        }
    }

    private fun phase(p: SplashContract.AnimationPhase) =
        _state.update { it.copy(phase = p) }
}