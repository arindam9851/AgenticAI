package com.example.agenticai.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenticai.domain.usecase.AgenticAIUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val useCases: AgenticAIUseCases   // ← only dependency needed
) : ViewModel() {

    private val _state  = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state.asStateFlow()

    private val _effect = Channel<CartEffect>(Channel.BUFFERED)
    val effect          = _effect.receiveAsFlow()

    init { loadCart() }

    fun onIntent(intent: CartIntent) {
        when (intent) {
            is CartIntent.RemoveItem          -> removeItem(intent.itemId)
            is CartIntent.IncreaseQty         -> changeQuantity(intent.itemId, +1)
            is CartIntent.DecreaseQty         -> changeQuantity(intent.itemId, -1)
            is CartIntent.PlaceOrder          -> placeOrder()
            is CartIntent.ClearAll            -> clearAll()
            is CartIntent.DismissOrderSuccess -> _state.update { it.copy(orderPlaced = false) }
        }
    }

    private fun loadCart() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            useCases.getCart().fold(
                onSuccess = { items -> _state.update { it.copy(items = items, isLoading = false) } },
                onFailure = { e  -> _state.update { it.copy(isLoading = false, error = e.message) } }
            )
        }
    }

    private fun changeQuantity(itemId: String, delta: Int) {
        val item   = _state.value.items.find { it.id == itemId } ?: return
        val newQty = item.quantity + delta
        if (newQty <= 0) { removeItem(itemId); return }

        _state.update { s ->
            s.copy(items = s.items.map { if (it.id == itemId) it.copy(quantity = newQty) else it })
        }
        viewModelScope.launch {
            useCases.updateQty(itemId, newQty).onFailure { loadCart() }
        }
    }

    private fun removeItem(itemId: String) {
        _state.update { s -> s.copy(items = s.items.filter { it.id != itemId }) }
        viewModelScope.launch {
            useCases.removeFromCart(itemId).onFailure { loadCart() }
        }
    }

    private fun clearAll() {
        val backup = _state.value.items.toList()
        _state.update { it.copy(items = emptyList()) }
        viewModelScope.launch {
            useCases.clearCart().onFailure { _state.update { it.copy(items = backup) } }
        }
    }

    private fun placeOrder() {
        val items = _state.value.items
        if (items.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isPlacingOrder = true, error = null) }
            useCases.placeOrder(items).fold(
                onSuccess = { orderId ->
                    _state.update { it.copy(
                        items = emptyList(), isPlacingOrder = false,
                        orderPlaced = true, orderId = orderId
                    )}
                },
                onFailure = { e ->
                    _state.update { it.copy(isPlacingOrder = false, error = e.message) }
                }
            )
        }
    }
}