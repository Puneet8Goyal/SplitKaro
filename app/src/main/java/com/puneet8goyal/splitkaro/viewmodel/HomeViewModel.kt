package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.domain.ExpenseCalculator
import com.puneet8goyal.splitkaro.domain.ExpenseSummary
import com.puneet8goyal.splitkaro.domain.MemberBalance
import com.puneet8goyal.splitkaro.repository.ExpenseRepository
import com.puneet8goyal.splitkaro.repository.MemberRepository
import com.puneet8goyal.splitkaro.utils.AppUtils
import com.puneet8goyal.splitkaro.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val memberRepository: MemberRepository,
    private val expenseCalculator: ExpenseCalculator,
    private val userPreferences: UserPreferences
) : ViewModel() {

    var allExpenses by mutableStateOf<List<Expense>>(emptyList())
    var expenses by mutableStateOf<List<Expense>>(emptyList())
    var members by mutableStateOf<List<Member>>(emptyList())
    var expenseSummary by mutableStateOf(ExpenseSummary(0, 0.0, 0.0, 0.0, 0.0))
    var isLoading by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var searchQuery by mutableStateOf("")
    var isSearchActive by mutableStateOf(false)

    // FIXED: Get current user Member ID (not just random ID)
    fun getCurrentUserId(): Long = userPreferences.getCurrentUserMemberId()
    fun getCurrentUserName(): String? = userPreferences.getCurrentUserName()

    fun loadExpenses(collectionId: Long) {
        isLoading = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                allExpenses = expenseRepository.getExpensesByCollectionId(collectionId)
                members = memberRepository.getMembersByCollectionId(collectionId)

                // Verify current user is in this group
                val currentUserMemberId = userPreferences.getCurrentUserMemberId()
                val currentUserInGroup = members.find { it.id == currentUserMemberId }

                if (currentUserInGroup == null) {
                    println("DEBUG: WARNING - Current user not found in group members!")
                    println("DEBUG: Current user ID: $currentUserMemberId")
                    println("DEBUG: Members in group: ${members.map { "${it.id}:${it.name}" }}")
                } else {
                    println("DEBUG: Current user ${currentUserInGroup.name} found in group")
                }

                applyFilters()
                println("DEBUG: Loaded ${allExpenses.size} expenses for collectionId: $collectionId")
            } catch (e: Exception) {
                errorMessage = "Failed to load expenses. Please try again."
                println("DEBUG: Error loading expenses: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }


    fun refreshAfterSettlement(collectionId: Long) {
        viewModelScope.launch {
            try {
                // Force reload all data after settlement
                loadExpenses(collectionId)
                println("DEBUG Home: Refreshed data after settlement for collection $collectionId")
            } catch (e: Exception) {
                errorMessage = "Error refreshing after settlement: ${e.message}"
                println("DEBUG Home: Error refreshing after settlement: ${e.message}")
            }
        }
    }

    fun refreshExpenses(collectionId: Long) {
        isRefreshing = true
        errorMessage = ""
        viewModelScope.launch {
            try {
                allExpenses = expenseRepository.getExpensesByCollectionId(collectionId)
                members = memberRepository.getMembersByCollectionId(collectionId)
                applyFilters()
                println("DEBUG: Refreshed ${allExpenses.size} expenses")
            } catch (e: Exception) {
                errorMessage = "Refresh failed. Please try again."
                println("DEBUG: Error refreshing expenses: ${e.message}")
            } finally {
                isRefreshing = false
            }
        }
    }

    fun searchExpenses(query: String) {
        searchQuery = query
        isSearchActive = query.isNotBlank()
        if (errorMessage.isNotEmpty()) clearErrorMessage()
        applyFilters()
    }

    fun clearFilters() {
        searchQuery = ""
        isSearchActive = false
        applyFilters()
    }

    fun calculateMemberBalances(
        expenses: List<Expense>,
        members: List<Member>
    ): List<MemberBalance> {
        return expenseCalculator.calculateMemberBalances(expenses, members)
    }

    // FIXED: Calculate user-centric balances using correct Member ID
    fun calculateUserCentricBalances(
        expenses: List<Expense>,
        members: List<Member>
    ): List<com.puneet8goyal.splitkaro.domain.UserCentricBalance> {
        val currentUserId = userPreferences.getCurrentUserMemberId()
        return if (currentUserId != -1L) {
            expenseCalculator.calculateUserCentricBalances(expenses, members, currentUserId)
        } else {
            emptyList()
        }
    }

    // FIXED: Calculate current user's overall balance using correct Member ID
    fun calculateCurrentUserOverallBalance(
        expenses: List<Expense>,
        members: List<Member>
    ): Double {
        val currentUserId = userPreferences.getCurrentUserMemberId()
        return if (currentUserId != -1L) {
            expenseCalculator.calculateCurrentUserOverallBalance(expenses, members, currentUserId)
        } else {
            0.0
        }
    }

    private fun applyFilters() {
        expenses = AppUtils.filterExpenses(
            expenses = allExpenses,
            searchQuery = searchQuery,
            members = members
        )

        expenseSummary = expenseCalculator.calculateExpenseSummary(expenses)
    }

    fun getGroupedExpenses(): Map<String, List<Expense>> {
        return AppUtils.groupExpensesByDate(expenses)
    }

    fun clearErrorMessage() {
        errorMessage = ""
    }

    fun getFilteredExpenseCount(): String {
        return if (isSearchActive) {
            "Showing ${expenses.size} of ${allExpenses.size} expenses"
        } else {
            "${expenses.size} expenses"
        }
    }

    fun hasActiveFilters(): Boolean {
        return isSearchActive
    }
}
