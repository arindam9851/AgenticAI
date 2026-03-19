package com.example.agenticai.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agenticai.data.source.remote.GroqAgentClient
import com.example.agenticai.domain.model.Message
import com.example.agenticai.domain.model.Role
import com.example.agenticai.domain.repository.ConfigRepository
import com.example.agenticai.domain.usecase.AgenticAIUseCases
import com.example.agenticai.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val useCases: AgenticAIUseCases,
    private val configRepository: ConfigRepository,
    private val groqAgentClient: GroqAgentClient
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    private val _effect = Channel<ChatEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // Cached API key
    private var apiKey: String = ""

    init {
        initialize()
    }

    fun onIntent(intent: ChatIntent) {
        when (intent) {
            is ChatIntent.SendMessage  -> sendMessage(intent.text)
            is ChatIntent.UpdateInput  -> updateInput(intent.text)
            is ChatIntent.Retry        -> initialize()
            is ChatIntent.ClearError   -> clearError()
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            updateState { it.copy(isInitializing = true, error = null) }
            try {
                // Fetch API key + products in parallel
                apiKey = configRepository.getGroqApiKey()

                useCases.getProducts().fold(
                    onSuccess = { products ->
                        updateState {
                            it.copy(
                                isInitializing = false,
                                products= products
                            )
                        }
                    },
                    onFailure = { e ->
                        updateState {
                            it.copy(
                                isInitializing = false,
                                error= "Failed to load products: ${e.message}"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                updateState {
                    it.copy(
                        isInitializing = false,
                        error= "Initialization failed: ${e.message}"
                    )
                }
            }
        }
    }

    private fun sendMessage(text: String) {
        if (text.isBlank() || _state.value.isLoading) return

        val userMessage = Message(role = Role.USER, content = text)
        val updatedMessages = _state.value.messages + userMessage

        updateState {
            it.copy(
                messages  = updatedMessages,
                isLoading = true,
                inputText = "",
                error     = null
            )
        }

        viewModelScope.launch {
            _effect.send(ChatEffect.ScrollToBottom)
            try {
                val (reply, products) = groqAgentClient.chat(
                    apiKey   = apiKey,
                    history  = updatedMessages,
                    products = _state.value.products
                )

                val assistantMessage = Message(
                    role     = Role.ASSISTANT,
                    content  = reply,
                    products = products
                )

                updateState {
                    it.copy(
                        messages  = updatedMessages + assistantMessage,
                        isLoading = false
                    )
                }
                _effect.send(ChatEffect.ScrollToBottom)

            } catch (e: Exception) {
                val errorMessage = Message(
                    role= Role.ASSISTANT,
                    content = "Sorry, something went wrong: ${e.message}"
                )
                Log.e("ChatViewModel", "Error: ${e.message}")
                updateState {
                    it.copy(
                        messages  = updatedMessages + errorMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun updateInput(text: String) {
        updateState { it.copy(inputText = text) }
    }

    private fun clearError() {
        updateState { it.copy(error = null) }
    }

    private fun updateState(reducer: (ChatState) -> ChatState) {
        _state.update(reducer)
    }
}