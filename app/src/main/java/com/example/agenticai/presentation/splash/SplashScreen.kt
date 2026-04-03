package com.example.agenticai.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.agenticai.presentation.splash.components.AiBadge
import com.example.agenticai.presentation.splash.components.AppNameText
import com.example.agenticai.presentation.splash.components.FloatingParticles
import com.example.agenticai.presentation.splash.components.LogoCard
import com.example.agenticai.presentation.splash.components.PulsingRing
import com.example.agenticai.presentation.splash.components.TaglineText
import com.example.agenticai.ui.theme.AccentViolet
import com.example.agenticai.ui.theme.TextMuted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun SplashScreen(
    onNavigateToChat: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        launch {
            viewModel.effect.collectLatest { effect ->
                when (effect) {
                    SplashContract.Effect.NavigateToChat -> onNavigateToChat()
                }
            }
        }
        // collector is now active before animation starts
        viewModel.handleIntent(SplashContract.Intent.StartAnimation)
    }

    // Explicit type arg avoids ambiguity with Compose State<T> and coroutines' State alias
    val state by viewModel.state.collectAsState(initial = SplashContract.SplashState())

    SplashContent(phase = state.phase)
}

// ── Pure UI (no ViewModel dependency — easy to preview) ──────────────────────
@Composable
fun SplashContent(phase: SplashContract.AnimationPhase) {

    val showParticles = phase >= SplashContract.AnimationPhase.PARTICLES
    val showLogo = phase >= SplashContract.AnimationPhase.LOGO
    val showText = phase >= SplashContract.AnimationPhase.TEXT
    val doPulse = phase == SplashContract.AnimationPhase.PULSE
    val bg = MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        // ── Radial glow backdrop ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentViolet.copy(alpha = 0.12f), Color.Transparent),
                        radius = 900f,
                    )
                )
        )

        // ── Floating particles ────────────────────────────────────────────────
        if (showParticles) {
            FloatingParticles()
        }

        // ── Centre column: ring + logo + text ────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            // Ring + Logo stacked
            Box(contentAlignment = Alignment.Center) {
                if (showLogo) PulsingRing()
                if (showLogo) LogoCard(doPulse = doPulse)
            }

            Spacer(Modifier.height(32.dp))

            if (showText) {
                AppNameText()
                Spacer(Modifier.height(6.dp))
                TaglineText()
                Spacer(Modifier.height(14.dp))
                AiBadge()
            }
        }

        // ── Version footer ────────────────────────────────────────────────────
        Text(
            text = "v1.0.0",
            fontSize = 10.sp,
            color = TextMuted.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
        )
    }
}




