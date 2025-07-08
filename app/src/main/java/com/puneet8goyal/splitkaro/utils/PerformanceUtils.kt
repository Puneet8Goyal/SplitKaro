package com.puneet8goyal.splitkaro.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

object PerformanceUtils {

    // Debounce search input to avoid excessive API calls
    @Composable
    fun useDebounce(
        value: String,
        delayMillis: Long = 300L
    ): String {
        var debouncedValue by remember { mutableStateOf(value) }

        LaunchedEffect(value) {
            delay(delayMillis)
            debouncedValue = value
        }

        return debouncedValue
    }

    // Cache frequently used calculations
    class CalculationCache<K, V> {
        private val cache = mutableMapOf<K, V>()

        fun get(key: K, calculation: () -> V): V {
            return cache.getOrPut(key) { calculation() }
        }

        fun clear() {
            cache.clear()
        }

        fun remove(key: K) {
            cache.remove(key)
        }
    }

    // Memory-efficient list operations
    fun <T> List<T>.chunked(size: Int): List<List<T>> {
        return if (this.isEmpty()) emptyList()
        else this.windowed(size, size, true)
    }

    // Optimize string formatting for currency
    private val currencyFormatter by lazy {
        java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault())
    }

    fun formatCurrencyOptimized(amount: Double): String {
        return currencyFormatter.format(amount)
    }
}
