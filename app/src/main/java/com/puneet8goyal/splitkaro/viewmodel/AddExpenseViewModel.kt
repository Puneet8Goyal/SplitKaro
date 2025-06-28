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
class AddExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {

    var description by mutableStateOf("")
    var amount by mutableStateOf("")
    var paidByMemberId by mutableStateOf<Long?>(null)
    var splitAmongMemberIds by mutableStateOf<List<Long>>(emptyList())
    var snackbarMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun addExpense(collectionId: Long, onSuccess: () -> Unit) {
        if (isLoading) return

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

                val expense = Expense(
                    collectionId = collectionId,
                    description = description.trim(),
                    amount = expenseAmount,
                    paidByMemberId = paidByMemberId!!,
                    splitAmongMemberIds = splitAmongMemberIds,
                    perPersonAmount = perPerson,
                    createdAt = System.currentTimeMillis()
                )

                val result = expenseRepository.insertExpense(expense)
                result.fold(
                    onSuccess = {
                        clearInputs()
                        // FIXED: Don't show success snackbar here, pass to navigation
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
            amount.toDoubleOrNull()?.let { it <= 0 } != false -> "Amount must be greater than 0"
            paidByMemberId == null -> "Paid by is required"
            splitAmongMemberIds.isEmpty() -> "Select at least one person to split among"
            else -> null
        }
    }

    fun clearInputs() {
        description = ""
        amount = ""
        paidByMemberId = null
        splitAmongMemberIds = emptyList()
        snackbarMessage = ""
    }

    fun updateDescription(value: String) {
        description = value
        if (snackbarMessage.isNotEmpty()) clearErrorMessage()
    }

    fun updateAmount(value: String) {
        amount = value
        if (snackbarMessage.isNotEmpty()) clearErrorMessage()
    }

    fun updatePaidByMemberId(memberId: Long?) {
        paidByMemberId = memberId
        if (snackbarMessage.isNotEmpty()) clearErrorMessage()
    }

    fun updateSplitAmongMemberIds(memberIds: List<Long>) {
        splitAmongMemberIds = memberIds
        if (snackbarMessage.isNotEmpty()) clearErrorMessage()
    }

    fun clearErrorMessage() {
        snackbarMessage = ""
    }
}
