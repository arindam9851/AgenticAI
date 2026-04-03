package com.example.agenticai.presentation.splash.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agenticai.ui.theme.TextPrimary

@Composable
fun AppNameText() {
    val offsetY = remember { Animatable(40f) }
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        alpha.animateTo(1f, tween(400))
    }
    Text(
        text = "ShopMind",
        fontSize = 52.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        letterSpacing = 1.sp,
        modifier = Modifier
            .alpha(alpha.value)
            .offset(y = offsetY.value.dp),
    )
}