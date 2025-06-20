package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.domain.ExpenseCalculator
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
    var overallBalance by mutableStateOf(0.0)

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            expenses = expenseRepository.getAllExpenses()
            overallBalance = expenseCalculator.calculateOverallBalance(expenses)
        }
    }

    fun refreshExpenses() {
        loadExpenses()
    }

}