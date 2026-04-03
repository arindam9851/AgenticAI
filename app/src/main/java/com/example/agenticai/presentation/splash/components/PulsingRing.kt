package com.example.agenticai.presentation.splash.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.agenticai.ui.theme.AccentViolet

@Composable
fun PulsingRing() {
    val transition = rememberInfiniteTransition(label = "ring")
    val scale by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutLinearInEasing)),
        label = "ringScale",
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutLinearInEasing)),
        label = "ringAlpha",
    )
    Box(
        modifier = Modifier
            .size(120.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale; alpha = ringAlpha }
            .clip(CircleShape)
            .background(Color.Transparent)
            .drawWithContent {
                drawCircle(
                    color = AccentViolet,
                    radius = size.minDimension / 2,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()),
                )
            }
    )
}