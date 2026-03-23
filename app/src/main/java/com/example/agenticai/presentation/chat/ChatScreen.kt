package com.example.agenticai.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.agenticai.presentation.component.ChatBubble
import com.example.agenticai.presentation.component.TypingIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onCartClick: () -> Unit = {},
    onOrderClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Accent color — deep indigo/violet feel
    val accentColor = Color(0xFF6C5CE7)
    val accentLight = Color(0xFFEEEDFE)
    val accentDark = Color(0xFF3C3489)
    val bgColor = MaterialTheme.colorScheme.background

    val suggestions = listOf(
        "Electronics under 500 SEK",
        "Best rated products",
        "Show fitness products",
        "Cheapest home products",
        "Show sold out items",
        "Add best rated electronics to cart",
        "Best kitchen product under 300 SEK, buy it for me"
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ChatEffect.ScrollToBottom -> {
                    if (state.messages.isNotEmpty()) {
                        scope.launch {
                            listState.animateScrollToItem(state.messages.size - 1)
                        }
                    }
                }

                is ChatEffect.ShowError -> {}
            }
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            // ── Beautiful custom TopBar ───────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left — AI avatar + title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Pulsing AI avatar
                        Box(contentAlignment = Alignment.Center) {
                            val pulse = rememberInfiniteTransition(label = "pulse")
                            val scale by pulse.animateFloat(
                                initialValue = 1f,
                                targetValue = 1.15f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = EaseInOut),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "scale"
                            )
                            Box(
                                modifier = Modifier
                                    .size((36 * scale).dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AI",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Shopping Assistant",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Online indicator
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF00E676))
                                )
                                Text(
                                    text = if (state.isInitializing) "Loading..."
                                    else "${state.products.size} products · Online",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // ✅ Replace single IconButton with Row of two icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        // Orders icon
                        IconButton(
                            onClick  = onOrderClick,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector        = Icons.Default.ShoppingBag,
                                contentDescription = "My Orders",
                                tint               = Color.White,
                                modifier           = Modifier.size(20.dp)
                            )
                        }

                        // Cart icon
                        IconButton(
                            onClick  = onCartClick,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector        = Icons.Default.ShoppingCart,
                                contentDescription = "My Cart",
                                tint               = Color.White,
                                modifier           = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->

        if (state.isInitializing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = accentColor)
                    Text(
                        "Setting up your assistant...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── Error banner ──────────────────────────────────────────────────
            AnimatedVisibility(visible = state.error != null) {
                state.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            TextButton(onClick = { viewModel.onIntent(ChatIntent.Retry) }) {
                                Text("Retry", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            // ── Messages ──────────────────────────────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {

                // ── Empty state ───────────────────────────────────────────────
                if (state.messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight()
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                // Large AI circle
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(accentLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "AI",
                                        color = accentColor,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = "Hi! I'm your shopping assistant",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Ask me anything about products — I'll search, filter, compare and even buy for you!",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )

                                Spacer(Modifier.height(8.dp))

                                // Suggestion chips centered
                                suggestions.forEach { suggestion ->
                                    SuggestionChip(
                                        onClick = {
                                            viewModel.onIntent(ChatIntent.SendMessage(suggestion))
                                        },
                                        label = {
                                            Text(
                                                suggestion,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = accentDark,
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        shape = RoundedCornerShape(20.dp),
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = accentLight,
                                            labelColor = accentDark
                                        ),
                                        border = SuggestionChipDefaults.suggestionChipBorder(
                                            enabled = true,
                                            borderColor = accentColor.copy(alpha = 0.3f),
                                            borderWidth = 0.5.dp
                                        ),
                                        modifier = Modifier.fillMaxWidth(0.85f)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Chat messages ─────────────────────────────────────────────
                items(items = state.messages, key = { it.id }) { message ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                    ) {
                        ChatBubble(message = message)
                    }
                }

                if (state.isLoading) {
                    item { TypingIndicator() }
                }
            }

            // ── Persistent chips above input (after first message) ────────────
            AnimatedVisibility(
                visible = state.messages.isNotEmpty(),
                enter = fadeIn() + slideInVertically()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionChip(
                            onClick = {
                                viewModel.onIntent(ChatIntent.SendMessage(suggestion))
                            },
                            label = {
                                Text(
                                    suggestion,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = accentDark,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = accentLight,
                                labelColor = accentDark
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = accentColor.copy(alpha = 0.3f),
                                borderWidth = 0.5.dp
                            )
                        )
                    }
                }
            }

            // ── Input row ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 16.dp, top = 4.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = state.inputText,
                    onValueChange = { viewModel.onIntent(ChatIntent.UpdateInput(it)) },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Ask about products...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    enabled = !state.isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    )
                )

                // Send button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (state.inputText.isNotBlank() && !state.isLoading)
                                accentColor
                            else
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (state.inputText.isNotBlank() && !state.isLoading) {
                                viewModel.onIntent(ChatIntent.SendMessage(state.inputText.trim()))
                            }
                        },
                        modifier = Modifier.size(52.dp),
                        enabled = state.inputText.isNotBlank() && !state.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}