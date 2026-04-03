package com.example.agenticai.presentation.splash.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.agenticai.ui.theme.AccentAmber
import com.example.agenticai.ui.theme.AccentTeal
import com.example.agenticai.ui.theme.AccentViolet

@Composable
fun FloatingParticles() {
    data class Particle(
        val x: Float, val y: Float,
        val size: Dp, val color: Color,
        val durationMs: Int, val delayMs: Int,
    )

    val particles = remember {
        listOf(
            Particle(0.12f, 0.22f, 10.dp, AccentViolet, 1200, 0),
            Particle(0.82f, 0.30f, 6.dp, AccentAmber, 1000, 80),
            Particle(0.08f, 0.65f, 14.dp, AccentViolet.copy(alpha = 0.5f), 1400, 140),
            Particle(0.88f, 0.55f, 7.dp, AccentAmber.copy(alpha = 0.5f), 1100, 60),
            Particle(0.75f, 0.18f, 5.dp, AccentTeal.copy(alpha = 0.6f), 1300, 200),
        )
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        particles.forEachIndexed { i, p ->
            val transition = rememberInfiniteTransition(label = "p$i")
            val offsetY by transition.animateFloat(
                initialValue = 0f,
                targetValue = if (i % 2 == 0) -12f else 12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        p.durationMs,
                        delayMillis = p.delayMs,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "py$i",
            )
            val entryAlpha = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(p.delayMs.toLong())
                entryAlpha.animateTo(1f, tween(350))
            }

            Box(
                modifier = Modifier
                    .offset(x = w * p.x, y = h * p.y + offsetY.dp)
                    .size(p.size)
                    .alpha(entryAlpha.value)
                    .clip(CircleShape)
                    .background(p.color),
            )
        }
    }
}