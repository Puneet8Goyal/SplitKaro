package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.domain.ExpenseCalculator
import com.puneet8goyal.splitkaro.domain.ExpenseSummary
import com.puneet8goyal.splitkaro.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val expenseCalculator: ExpenseCalculator
) : ViewModel() {
    var expenses by mutableStateOf<List<Expense>>(emptyList())
    var expenseSummary by mutableStateOf(
        ExpenseSummary(
            totalExpenses = 0,
            totalAmount = 0.0,
            userShare = 0.0,
            overallBalance = 0.0,
            averageExpense = 0.0
        )
    )
    var isLoading by mutableStateOf(false)

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        isLoading = true
        viewModelScope.launch {
            try {
                expenses = expenseRepository.getAllExpenses()
                expenseSummary = expenseCalculator.calculateExpenseSummary(expenses)
            } catch (e: Exception) {
                // Handle error if needed
                println("Error loading expenses: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun refreshExpenses() {
        loadExpenses()
    }

    fun getExpensesByPayer(): Map<String, List<Expense>> {
        return expenseCalculator.getExpensesByPayer(expenses)
    }
}