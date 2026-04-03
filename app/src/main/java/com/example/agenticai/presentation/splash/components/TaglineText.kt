package com.example.agenticai.presentation.splash.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agenticai.ui.theme.TextMuted

@Composable
fun TaglineText() {
    val offsetY = remember { Animatable(30f) }
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(150)
        offsetY.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
        alpha.animateTo(1f, tween(400))
    }
    Text(
        text = "YOUR AI SHOPPING AGENT",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 2.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .alpha(alpha.value)
            .offset(y = offsetY.value.dp),
    )
}
