package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.repository.interfaces.ExpenseRepositoryInterface
import com.puneet8goyal.splitkaro.repository.interfaces.MemberRepositoryInterface
import com.puneet8goyal.splitkaro.utils.AppConfig
import com.puneet8goyal.splitkaro.utils.ValidationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepositoryInterface,
    private val memberRepository: MemberRepositoryInterface,
    private val appConfig: AppConfig
) : ViewModel() {

    private val validationHelper = ValidationHelper(appConfig)

    var description by mutableStateOf("")
        private set
    var amount by mutableStateOf("")
        private set
    var paidByMemberId by mutableStateOf<Long?>(null)
        private set
    var splitAmongMemberIds by mutableStateOf<List<Long>>(emptyList())
        private set
    var snackbarMessage by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun addExpense(collectionId: Long, onSuccess: () -> Unit) {
        if (isLoading) return

        snackbarMessage = ""
        val validationError = validateAllInputs()
        if (validationError != null) {
            snackbarMessage = validationError
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val expenseAmount = amount.toDouble()
                val splitCount = splitAmongMemberIds.size
                val perPersonAmount = expenseAmount / splitCount

                val expense = Expense(
                    collectionId = collectionId,
                    description = description.trim(),
                    amount = expenseAmount,
                    paidByMemberId = paidByMemberId!!,
                    splitAmongMemberIds = splitAmongMemberIds,
                    perPersonAmount = perPersonAmount,
                    createdAt = System.currentTimeMillis()
                )

                expenseRepository.insertExpense(expense).fold(
                    onSuccess = {
                        clearForm()
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

    private fun validateAllInputs(): String? {
        validationHelper.validateExpenseDescription(description)?.let { return it }
        validationHelper.validateAmount(amount)?.let { return it }
        validationHelper.validatePaidBy(paidByMemberId)?.let { return it }
        validationHelper.validateSplitMembers(splitAmongMemberIds)?.let { return it }
        return null
    }

    private fun clearForm() {
        description = ""
        amount = ""
        paidByMemberId = null
        splitAmongMemberIds = emptyList()
        snackbarMessage = ""
    }

    fun updateDescription(value: String) {
        description = value
        clearErrorIfExists()
    }

    fun updateAmount(value: String) {
        amount = value
        clearErrorIfExists()
    }

    fun updatePaidByMemberId(memberId: Long?) {
        paidByMemberId = memberId
        clearErrorIfExists()
    }

    fun updateSplitAmongMemberIds(memberIds: List<Long>) {
        splitAmongMemberIds = memberIds
        clearErrorIfExists()
    }

    fun clearErrorMessage() {
        snackbarMessage = ""
    }

    private fun clearErrorIfExists() {
        if (snackbarMessage.isNotEmpty()) {
            snackbarMessage = ""
        }
    }
}
