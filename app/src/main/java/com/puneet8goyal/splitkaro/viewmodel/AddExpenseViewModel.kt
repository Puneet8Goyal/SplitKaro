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
class AddExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    var description by mutableStateOf("")
    var amount by mutableStateOf("")
    var paidBy by mutableStateOf("")
    var splitAmong by mutableStateOf("")
    var splitAmongPeople by mutableStateOf("") // Names of people involved
    var snackbarMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun addExpense(groupId:Long, onSuccess: () -> Unit) {
        if (isLoading) return

        val validationError = validateInputs()
        if (validationError != null) {
            snackbarMessage = validationError
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val expenseAmount = amount.toDouble()
                val splitCount = splitAmong.toInt()
                val perPerson = expenseAmount / splitCount

                val expense = Expense(
                    groupId=groupId,
                    description = description.trim(),
                    amount = expenseAmount,
                    paidBy = paidBy.trim(),
                    splitAmong = splitCount,
                    perPersonAmount = perPerson
                )

                val result = expenseRepository.insertExpense(expense)
                result.fold(
                    onSuccess = {
                        clearInputs()
                        snackbarMessage = "Expense added successfully!"
                        onSuccess()
                    },
                    onFailure = { exception ->
                        snackbarMessage = "Error adding expense: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                snackbarMessage = "Error adding expense: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun validateInputs(): String? {
        return when {
            description.trim().isEmpty() -> "Description is required"
            amount.isEmpty() -> "Amount is required"
            amount.toDoubleOrNull() == null -> "Invalid amount format"
            amount.toDoubleOrNull()!! <= 0 -> "Amount must be greater than 0"
            paidBy.trim().isEmpty() -> "Paid by field is required"
            splitAmong.isEmpty() -> "Split among field is required"
            splitAmong.toIntOrNull() == null -> "Invalid number for split among"
            splitAmong.toIntOrNull()!! <= 0 -> "Split among must be greater than 0"
            splitAmong.toIntOrNull()!! > 50 -> "Cannot split among more than 50 people"
            else -> null
        }
    }

    fun clearInputs() {
        description = ""
        amount = ""
        paidBy = ""
        splitAmong = ""
        splitAmongPeople = ""
        snackbarMessage = ""
    }

    fun clearMessage() {
        snackbarMessage = ""
    }
}