package com.example.agenticai.domain.model

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: Role = Role.USER,
    val content: String = "",
    val products: List<Product> = emptyList(),  // populated when AI returns product results
    val isToolCall: Boolean = false
)

enum class Role { USER, ASSISTANT, TOOL }