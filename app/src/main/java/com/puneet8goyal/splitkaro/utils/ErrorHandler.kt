package com.puneet8goyal.splitkaro.utils

import android.content.Context
import kotlinx.coroutines.TimeoutCancellationException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorHandler(private val context: Context) {

    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException -> "No internet connection. Please check your network."
            is SocketTimeoutException -> "Connection timeout. Please try again."
            is TimeoutCancellationException -> "Operation timed out. Please try again."
            is IOException -> "Network error occurred. Please try again."
            is IllegalArgumentException -> "Invalid data provided. Please check your input."
            is IllegalStateException -> "App is in an invalid state. Please restart the app."
            else -> throwable.message ?: "An unexpected error occurred. Please try again."
        }
    }

    fun getErrorMessageWithSuggestion(throwable: Throwable): Pair<String, String> {
        return when (throwable) {
            is UnknownHostException ->
                "No Internet Connection" to "Please check your WiFi or mobile data and try again."

            is SocketTimeoutException ->
                "Connection Timeout" to "Your connection is slow. Please try again in a few moments."

            is IOException ->
                "Network Error" to "Please check your internet connection and try again."

            else ->
                "Something Went Wrong" to "Please try again. If the problem persists, restart the app."
        }
    }

    fun isNetworkError(throwable: Throwable): Boolean {
        return throwable is UnknownHostException ||
                throwable is SocketTimeoutException ||
                throwable is IOException
    }

    fun shouldRetry(throwable: Throwable): Boolean {
        return isNetworkError(throwable) || throwable is TimeoutCancellationException
    }
}
