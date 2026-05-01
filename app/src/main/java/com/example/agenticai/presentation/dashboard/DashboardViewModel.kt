package com.example.agenticai.presentation.dashboard

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


@HiltViewModel
class DashboardViewModel @Inject constructor(
) : ViewModel()  {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun processIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.OnTabSelected -> {
                _state.update { it.copy(selectedRoute = intent.route) }
            }
        }
    }
}