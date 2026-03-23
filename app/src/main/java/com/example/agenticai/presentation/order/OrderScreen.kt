package com.example.agenticai.presentation.order

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.agenticai.domain.model.Order
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onNavigateBack: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state       by viewModel.state.collectAsState()
    val accentColor  = Color(0xFF6C5CE7)
    val accentLight  = Color(0xFFEEEDFE)
    val successColor = Color(0xFF1D9E75)
    val successLight = Color(0xFFE1F5EE)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back button
                    IconButton(
                        onClick  = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Text("←", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    // Title
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "My Orders",
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                        Text(
                            if (state.isEmpty) "No orders yet"
                            else "${state.orders.size} order${if (state.orders.size > 1) "s" else ""} placed",
                            color    = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }

                    // Refresh button
                    IconButton(
                        onClick  = { viewModel.onIntent(OrderIntent.Refresh) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Text("↻", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    ) { padding ->

        when {

            // ── Loading ───────────────────────────────────────────────────────
            state.isLoading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = accentColor)
                        Text(
                            "Loading orders...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Error ─────────────────────────────────────────────────────────
            state.error != null -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("Something went wrong", fontWeight = FontWeight.Bold)
                        Text(
                            state.error ?: "",
                            style     = MaterialTheme.typography.bodySmall,
                            color     = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.onIntent(OrderIntent.Refresh) },
                            colors  = ButtonDefaults.buttonColors(containerColor = accentColor)
                        ) {
                            Text("Try Again", color = Color.White)
                        }
                    }
                }
            }

            // ── Empty state ───────────────────────────────────────────────────
            state.isEmpty -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(accentLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📦", fontSize = 40.sp)
                        }
                        Text(
                            "No orders yet",
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Ask the AI to find products and place an order for you!",
                            style     = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color     = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick  = onNavigateBack,
                            colors   = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape    = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text(
                                "Start Shopping",
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // ── Orders list ───────────────────────────────────────────────────
            else -> {
                LazyColumn(
                    modifier        = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding  = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = state.orders,
                        key   = { it.id }
                    ) { order ->
                        AnimatedVisibility(
                            visible = true,
                            enter   = fadeIn() + slideInVertically()
                        ) {
                            OrderCard(
                                order        = order,
                                accentColor  = accentColor,
                                successColor = successColor,
                                successLight = successLight
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    accentColor: Color,
    successColor: Color,
    successLight: Color
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.getDefault())
    val dateStr    = order.placedAt?.toDate()?.let { dateFormat.format(it) } ?: "—"

    Card(
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier  = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text       = "Order #${order.id.take(8)}",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Status badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = successLight
                ) {
                    Text(
                        text     = order.status.replaceFirstChar { it.uppercase() },
                        color    = successColor,
                        style    = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(
                thickness = 0.5.dp,
                color     = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(Modifier.height(10.dp))

            // ── Items count ───────────────────────────────────────────────────
            Text(
                text  = "${order.items.size} item${if (order.items.size > 1) "s" else ""}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // ── Items list ────────────────────────────────────────────────────
            order.items.forEach { item ->
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Bullet dot
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.5f))
                        )
                        Text(
                            text     = "${item.name} x${item.quantity}",
                            style    = MaterialTheme.typography.bodySmall,
                            color    = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text       = "${item.total} SEK",
                        style      = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(
                thickness = 0.5.dp,
                color     = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(Modifier.height(10.dp))

            // ── Total ─────────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Total",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text       = "${order.total} SEK",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = accentColor
                )
            }
        }
    }
}
