package com.example.agenticai.presentation.chat

import com.example.agenticai.domain.model.Message
import com.example.agenticai.domain.model.Product


/** Everything the UI can observe */
data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean= false,
    val isInitializing: Boolean= true,
    val inputText: String= "",
    val error: String?= null,
    val products: List<Product>    = emptyList()  // full catalog loaded at start
)

/** Every action a user can trigger */
sealed class ChatIntent {
    data class SendMessage(val text: String)  : ChatIntent()
    data class UpdateInput(val text: String)  : ChatIntent()
    object Retry: ChatIntent()
    object ClearError: ChatIntent()
}

/** One-time side effects (snackbars, navigation, etc.) */
sealed class ChatEffect {
    data class ShowError(val message: String) : ChatEffect()
    object ScrollToBottom                     : ChatEffect()
}