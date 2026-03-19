package com.example.agenticai.presentation.chat

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agenticai.presentation.component.ChatBubble
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.agenticai.presentation.component.TypingIndicator
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state     by viewModel.state.collectAsState()
    val listState  = rememberLazyListState()
    val scope      = rememberCoroutineScope()

    val suggestions = listOf(
        "Show electronics under 500 SEK",   // → filter_by_price + filter_by_category
        "Best rated products",              // → sort_by_rating(order=desc)
        "Fitness products",                 // → filter_by_category(fitness)
        "Show sold out items",              // → filter_by_stock(in_stock="false")
        "Show all categories"               // → get_categories
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
                is ChatEffect.ShowError -> { }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text       = "AI Shopping Assistant",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text  = if (state.isInitializing) "Loading..."
                            else "${state.products.size} products loaded",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { padding ->

        if (state.isInitializing) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Loading products...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {

                // ── Error banner ──────────────────────────────────────────────
                state.error?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        colors   = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier              = Modifier.padding(12.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text     = error,
                                modifier = Modifier.weight(1f),
                                style    = MaterialTheme.typography.bodySmall,
                                color    = MaterialTheme.colorScheme.onErrorContainer
                            )
                            TextButton(onClick = { viewModel.onIntent(ChatIntent.Retry) }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                // ── Messages list ─────────────────────────────────────────────
                LazyColumn(
                    state               = listState,
                    modifier            = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding      = PaddingValues(vertical = 12.dp)
                ) {
                    // ── Empty state — chips shown in center ───────────────────
                    if (state.messages.isEmpty()) {
                        item {
                            Box(
                                modifier         = Modifier
                                    .fillParentMaxHeight()
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment   = Alignment.CenterHorizontally,
                                    verticalArrangement   = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text       = "Hi! How can I help you shop today?",
                                        style      = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    suggestions.forEach { suggestion ->
                                        SuggestionChip(
                                            onClick = {
                                                viewModel.onIntent(ChatIntent.SendMessage(suggestion))
                                            },
                                            label = {
                                                Text(
                                                    suggestion,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── Chat messages ─────────────────────────────────────────
                    items(items = state.messages, key = { it.id }) { message ->
                        ChatBubble(message = message)
                    }

                    if (state.isLoading) {
                        item { TypingIndicator() }
                    }
                }

                // ── Suggestion chips — always visible above input ──────────────
                if (state.messages.isNotEmpty()) {
                    LazyRow(
                        modifier            = Modifier
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
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                }

                // ── Input row ─────────────────────────────────────────────────
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value         = state.inputText,
                        onValueChange = { viewModel.onIntent(ChatIntent.UpdateInput(it)) },
                        modifier      = Modifier.weight(1f),
                        placeholder   = { Text("Ask about products...") },
                        shape         = RoundedCornerShape(24.dp),
                        maxLines      = 4,
                        enabled       = !state.isLoading
                    )

                    FloatingActionButton(
                        onClick        = {
                            viewModel.onIntent(ChatIntent.SendMessage(state.inputText.trim()))
                        },
                        modifier       = Modifier.size(52.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary,
                        elevation      = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Send,
                            contentDescription = "Send"
                        )
                    }
                }
            }
        }
    }
}