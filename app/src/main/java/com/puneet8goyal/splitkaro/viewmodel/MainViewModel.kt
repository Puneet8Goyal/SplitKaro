package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    var title by mutableStateOf("")
    var amount by mutableStateOf("")
    var paidBy by mutableStateOf("")
    var groupId by mutableStateOf("")
    var expenses by mutableStateOf<List<Expense>>(emptyList())
    var snackbarMessage by mutableStateOf("")
    var selectedExpense by mutableStateOf<Expense?>(null)

    init {
        // Load expenses when ViewModel initializes
        viewModelScope.launch {
            val defaultGroupId = 1L // Assuming a default group ID for now
            expenses = expenseRepository.getExpensesForGroup(defaultGroupId)
        }
    }

    fun addExpense() {
        viewModelScope.launch {
            try {
                val expenseAmount = amount.toDoubleOrNull() ?: 0.0
                val expenseGroupId = groupId.toLongOrNull() ?: 0L
                if (title.isNotEmpty() && amount.isNotEmpty() && paidBy.isNotEmpty() && groupId.isNotEmpty()) {
                    val expense = Expense(
                        title = title,
                        amount = expenseAmount,
                        paidBy = paidBy,
                        groupId = expenseGroupId
                    )
                    expenseRepository.insertExpense(expense)
                    expenses = expenseRepository.getExpensesForGroup(expenseGroupId)
                    clearInputs()
                } else {
                    snackbarMessage = "All fields are required!"
                }
            } catch (e: NumberFormatException) {
                snackbarMessage = "Invalid number format for Amount or Group ID!"
            }
        }
    }

    fun editExpense(updatedExpense: Expense) {
        viewModelScope.launch {
            expenseRepository.updateExpense(updatedExpense)
            expenses = expenseRepository.getExpensesForGroup(updatedExpense.groupId)
            selectedExpense = null
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense)
            expenses = expenseRepository.getExpensesForGroup(expense.groupId)
        }
    }

    fun getExpensesForGroup(): List<Expense> {
        return expenses
    }

    fun clearInputs() {
        title = ""
        amount = ""
        paidBy = ""
        groupId = ""
    }

    fun clearSnackbar() {
        snackbarMessage = ""
    }

    fun selectExpenseForEdit(expense: Expense) {
        selectedExpense = expense
        title = expense.title
        amount = expense.amount.toString()
        paidBy = expense.paidBy
        groupId = expense.groupId.toString()
    }
}