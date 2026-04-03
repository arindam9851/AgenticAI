package com.example.agenticai.presentation.splash

object SplashContract {
    // ── Intent ────────────────────────────────────────────────────────────────
    sealed interface Intent {
        data object StartAnimation : Intent
        data object AnimationFinished : Intent
    }

    // ── State ─────────────────────────────────────────────────────────────────
    // Renamed SplashState — avoids collision with Compose's State<T>
    // and Kotlin coroutines' internal typealias State = Int
    data class SplashState(
        val phase: AnimationPhase = AnimationPhase.IDLE,
    )

    enum class AnimationPhase {
        IDLE, PARTICLES,   // 0 – 400 ms
        LOGO,        // 400 – 900 ms
        TEXT,        // 900 – 1 300 ms
        PULSE,       // 1 300 – 2 200 ms
        DONE,
    }

    // ── Effect ────────────────────────────────────────────────────────────────
    sealed interface Effect {
        data object NavigateToChat : Effect
    }
}