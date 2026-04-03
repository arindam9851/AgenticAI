package com.example.agenticai.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenticai.domain.usecase.order_usecase.OrderAllUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val useCase: OrderAllUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(OrderState())
    val state: StateFlow<OrderState> = _state.asStateFlow()

    init { loadOrders() }

    fun onIntent(intent: OrderIntent) {
        when (intent) {
            is OrderIntent.Refresh -> loadOrders()
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            useCase.getOrders().fold(
                onSuccess = { orders ->
                    _state.update { it.copy(orders = orders, isLoading = false) }
                },
                onFailure = { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            )
        }
    }
}
