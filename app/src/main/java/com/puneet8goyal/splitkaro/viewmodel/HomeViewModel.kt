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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val memberRepository: MemberRepository,
    private val expenseCalculator: ExpenseCalculator
) : ViewModel() {

    var allExpenses by mutableStateOf<List<Expense>>(emptyList())
    var expenses by mutableStateOf<List<Expense>>(emptyList())
    var members by mutableStateOf<List<Member>>(emptyList())
    var expenseSummary by mutableStateOf(ExpenseSummary(0, 0.0, 0.0, 0.0, 0.0))
    var isLoading by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false) // Add this to ViewModel
    var errorMessage by mutableStateOf("")

    var searchQuery by mutableStateOf("")
    var isSearchActive by mutableStateOf(false)

    fun loadExpenses(collectionId: Long) {
        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                allExpenses = expenseRepository.getExpensesByCollectionId(collectionId)
                members = memberRepository.getMembersByCollectionId(collectionId)
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

    // Simplified refresh function
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
                isRefreshing = false // Directly set in ViewModel
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