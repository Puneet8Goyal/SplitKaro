package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.repository.ExpenseRepository
import com.puneet8goyal.splitkaro.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    var description by mutableStateOf("")
    var amount by mutableStateOf("")
    var paidByMemberId by mutableStateOf<Long?>(null)
    var splitAmongMemberIds by mutableStateOf<List<Long>>(emptyList())
    var snackbarMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var expense by mutableStateOf<Expense?>(null)

    fun loadExpense(expenseId: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                val loadedExpense = expenseRepository.getExpenseById(expenseId)
                if (loadedExpense != null) {
                    expense = loadedExpense
                    description = loadedExpense.description
                    amount = loadedExpense.amount.toString()
                    paidByMemberId = loadedExpense.paidByMemberId
                    splitAmongMemberIds = loadedExpense.splitAmongMemberIds
                    println("DEBUG: Loaded expense for editing: ${loadedExpense.description}")
                } else {
                    snackbarMessage = "Expense not found"
                }
            } catch (e: Exception) {
                snackbarMessage = "Error loading expense: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateExpense(onSuccess: () -> Unit) {
        if (isLoading) return
        val currentExpense = expense ?: return

        snackbarMessage = ""
        val validationError = validateInputs()
        if (validationError != null) {
            snackbarMessage = validationError
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val expenseAmount = amount.toDouble()
                val splitCount = splitAmongMemberIds.size
                val perPerson = expenseAmount / splitCount

                val updatedExpense = currentExpense.copy(
                    description = description.trim(),
                    amount = expenseAmount,
                    paidByMemberId = paidByMemberId!!,
                    splitAmongMemberIds = splitAmongMemberIds,
                    perPersonAmount = perPerson
                )

                val result = expenseRepository.updateExpense(updatedExpense)
                result.fold(
                    onSuccess = {
                        snackbarMessage = "Expense updated successfully!"
                        onSuccess()
                    },
                    onFailure = { exception ->
                        snackbarMessage = "Error updating expense: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                snackbarMessage = "Error updating expense: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteExpense(onSuccess: () -> Unit) {
        val currentExpense = expense ?: return
        isLoading = true
        viewModelScope.launch {
            try {
                val result = expenseRepository.deleteExpense(currentExpense)
                result.fold(
                    onSuccess = {
                        snackbarMessage = "Expense deleted successfully!"
                        onSuccess()
                    },
                    onFailure = { exception ->
                        snackbarMessage = "Error deleting expense: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                snackbarMessage = "Error deleting expense: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun validateInputs(): String? {
        return when {
            description.trim().isEmpty() -> "Description is required"
            amount.isEmpty() -> "Amount is required"
            amount.toDoubleOrNull()?.let { it <= 0 } != false -> "Amount must be greater than 0"
            paidByMemberId == null -> "Paid by is required"
            splitAmongMemberIds.isEmpty() -> "Select at least one person to split among"
            else -> null
        }
    }

    fun updateDescription(value: String) {
        description = value
        // IMPLEMENTED: Auto-clear errors when user types
        if (snackbarMessage.isNotEmpty()) clearErrorMessage()
    }

    fun updateAmount(value: String) {
        amount = value
        // IMPLEMENTED: Auto-clear errors when user types
        if (snackbarMessage.isNotEmpty()) clearErrorMessage()
    }

    fun updatePaidByMemberId(memberId: Long?) {
        paidByMemberId = memberId
        // IMPLEMENTED: Auto-clear errors when user selects
        if (snackbarMessage.isNotEmpty()) clearErrorMessage()
    }

    fun updateSplitAmongMemberIds(memberIds: List<Long>) {
        splitAmongMemberIds = memberIds
        // IMPLEMENTED: Auto-clear errors when user selects
        if (snackbarMessage.isNotEmpty()) clearErrorMessage()
    }

    // IMPLEMENTED: Error message clearing
    fun clearErrorMessage() {
        snackbarMessage = ""
    }
}
