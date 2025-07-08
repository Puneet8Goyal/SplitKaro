package com.puneet8goyal.splitkaro.utils

// Unified UI State for all ViewModels
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val exception: Throwable? = null) : UiState<Nothing>()
}

// Enhanced Message System
data class UiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val type: MessageType = MessageType.INFO,
    val duration: Long = 3000L,
    val dismissible: Boolean = true
)

enum class MessageType {
    SUCCESS, ERROR, WARNING, INFO
}

// State management helpers
fun <T> UiState<T>.isLoading(): Boolean = this is UiState.Loading
fun <T> UiState<T>.isSuccess(): Boolean = this is UiState.Success
fun <T> UiState<T>.isError(): Boolean = this is UiState.Error
fun <T> UiState<T>.getDataOrNull(): T? = (this as? UiState.Success)?.data
fun <T> UiState<T>.getErrorOrNull(): String? = (this as? UiState.Error)?.message
