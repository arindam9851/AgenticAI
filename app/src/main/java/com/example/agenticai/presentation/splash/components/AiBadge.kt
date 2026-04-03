package com.example.agenticai.presentation.splash.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agenticai.ui.theme.AccentViolet

@Composable
fun AiBadge() {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(250)
        scale.animateTo(
            1f,
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        alpha.animateTo(1f, tween(300))
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = AccentViolet.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, AccentViolet.copy(alpha = 0.5f)),
        modifier = Modifier
            .alpha(alpha.value)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value },
    ) {
        Text(
            text = "✦  Powered by Llama Groq AI",
            fontSize = 15.sp,
            color = AccentViolet,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        )
    }
}