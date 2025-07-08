package com.puneet8goyal.splitkaro.utils

import androidx.compose.ui.graphics.Color
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.Member
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppUtils {

    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return formatter.format(amount)
    }

    fun getInitials(name: String): String {
        return name.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()
    }

    fun getAvatarColor(name: String): Color {
        val colors = listOf(
            Color(0xFF4CAF50), // Green
            Color(0xFF2196F3), // Blue
            Color(0xFFFF9800), // Orange
            Color(0xFF9C27B0), // Purple
            Color(0xFFE91E63), // Pink
            Color(0xFF00BCD4), // Cyan
            Color(0xFFFF5722), // Deep Orange
            Color(0xFF3F51B5), // Indigo
            Color(0xFF8BC34A), // Light Green
            Color(0xFFFF6F00)  // Amber
        )
        return colors[name.hashCode().mod(colors.size)]
    }

    // NEW: Filter expenses based on search query
    fun filterExpenses(
        expenses: List<Expense>,
        searchQuery: String,
        members: List<Member>
    ): List<Expense> {
        if (searchQuery.isBlank()) return expenses

        val query = searchQuery.lowercase().trim()

        return expenses.filter { expense ->
            // Search in description
            expense.description.lowercase().contains(query) ||
                    // Search in payer name
                    members.find { it.id == expense.paidByMemberId }?.name?.lowercase()
                        ?.contains(query) == true ||
                    // Search in amount
                    expense.amount.toString().contains(query)
        }
    }

    // NEW: Group expenses by date
    fun groupExpensesByDate(expenses: List<Expense>): Map<String, List<Expense>> {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val today = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val yesterday = SimpleDateFormat(
            "MMM dd, yyyy",
            Locale.getDefault()
        ).format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))

        return expenses
            .sortedByDescending { it.createdAt }
            .groupBy { expense ->
                val expenseDate = dateFormat.format(Date(expense.createdAt))
                when (expenseDate) {
                    today -> "Today"
                    yesterday -> "Yesterday"
                    else -> expenseDate
                }
            }
    }

    fun formatDate(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return dateTimeFormat.format(Date(timestamp))
    }
}
