package com.example.agenticai.presentation.splash.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agenticai.ui.theme.AccentAmber

@Composable
fun LogoCard(doPulse: Boolean) {
    // Rotate-in + scale-in on first appearance
    val rotation = remember { Animatable(-180f) }
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
        )
    }
    LaunchedEffect(Unit) {
        scale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(400))
    }

    // Breathing pulse once in PULSE phase
    val pulseScale by animateFloatAsState(
        targetValue = if (doPulse) 1.07f else 1f,
        animationSpec = if (doPulse)
            infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse)
        else tween(300),
        label = "pulse",
    )

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .size(88.dp)
            .alpha(alpha.value)
            .graphicsLayer {
                scaleX = scale.value * pulseScale
                scaleY = scale.value * pulseScale
                rotationZ = rotation.value
            },
    ) {
        // Gradient card
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFA78BFF), Color(0xFF7C5CFC)),
                    )
                ),
        ) {
            Text(text = "🛍", fontSize = 58.sp)
        }

        // Amber spark badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(22.dp)
                .offset(x = 4.dp, y = (-4).dp)
                .clip(CircleShape)
                .background(AccentAmber),
        ) {
            Text("✦", fontSize = 10.sp, color = Color(0xFF3A2800))
        }
    }
}