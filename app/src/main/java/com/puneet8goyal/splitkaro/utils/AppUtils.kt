package com.puneet8goyal.splitkaro.utils

import androidx.compose.ui.graphics.Color
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.Member
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

object AppUtils {

    // Currency Formatting
    fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        return formatter.format(amount).replace("₹", "₹")
    }

    // Member Avatar Colors
    private val avatarColors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFF00BCD4), // Cyan
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF8BC34A), // Light Green
        Color(0xFF3F51B5), // Indigo
        Color(0xFFCDDC39) // Lime
    )

    fun getAvatarColor(name: String): Color {
        return avatarColors[name.hashCode().mod(avatarColors.size)]
    }

    fun getInitials(name: String): String {
        return name.trim().split(" ")
            .take(2)
            .map { it.first().uppercaseChar() }
            .joinToString("")
    }

    // Date Grouping
    fun getDateGroup(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(Calendar.YEAR)

        calendar.timeInMillis = timestamp
        val expenseDay = calendar.get(Calendar.DAY_OF_YEAR)
        val expenseYear = calendar.get(Calendar.YEAR)

        return when {
            expenseYear == todayYear && expenseDay == today -> "Today"
            expenseYear == todayYear && expenseDay == today - 1 -> "Yesterday"
            expenseYear == todayYear && expenseDay >= today - 7 -> "This Week"
            expenseYear == todayYear && expenseDay >= today - 30 -> "This Month"
            else -> "Earlier"
        }
    }

    // Search & Filter
    fun filterExpenses(
        expenses: List<Expense>,
        searchQuery: String = "",
        members: List<Member> = emptyList()
    ): List<Expense> {
        return expenses.filter { expense ->
            val matchesSearch = if (searchQuery.isBlank()) true else {
                expense.description.contains(searchQuery, ignoreCase = true) ||
                        members.find { it.id == expense.paidByMemberId }?.name?.contains(
                            searchQuery,
                            ignoreCase = true
                        ) == true
            }

            matchesSearch
        }
    }

    // Group expenses by date
    fun groupExpensesByDate(expenses: List<Expense>): Map<String, List<Expense>> {
        return expenses.groupBy { getDateGroup(it.createdAt) }
            .toSortedMap(compareBy {
                when (it) {
                    "Today" -> 0
                    "Yesterday" -> 1
                    "This Week" -> 2
                    "This Month" -> 3
                    else -> 4
                }
            })
    }
}
