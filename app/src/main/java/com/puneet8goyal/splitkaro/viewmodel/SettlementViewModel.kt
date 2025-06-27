package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.data.SettlementRecord
import com.puneet8goyal.splitkaro.domain.ExpenseCalculator
import com.puneet8goyal.splitkaro.domain.MemberBalance
import com.puneet8goyal.splitkaro.domain.Settlement
import com.puneet8goyal.splitkaro.repository.ExpenseRepository
import com.puneet8goyal.splitkaro.repository.MemberRepository
import com.puneet8goyal.splitkaro.repository.SettlementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettlementViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val memberRepository: MemberRepository,
    private val settlementRepository: SettlementRepository,  // RESTORED
    private val expenseCalculator: ExpenseCalculator
) : ViewModel() {

    var expenses by mutableStateOf<List<Expense>>(emptyList())
    var members by mutableStateOf<List<Member>>(emptyList())
    var memberBalances by mutableStateOf<List<MemberBalance>>(emptyList())
    var settlements by mutableStateOf<List<SettlementWithStatus>>(emptyList())  // RESTORED
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    private var currentCollectionId: Long = -1L

    // RESTORED: Settlement with status tracking
    data class SettlementWithStatus(
        val settlement: Settlement,
        val settlementRecord: SettlementRecord? = null,
        val isSettled: Boolean = false
    )

    fun loadSettlementData(collectionId: Long) {
        currentCollectionId = collectionId
        isLoading = true
        errorMessage = ""

        viewModelScope.launch {
            try {
                val expensesList = expenseRepository.getExpensesByCollectionId(collectionId)
                val membersList = memberRepository.getMembersByCollectionId(collectionId)

                expenses = expensesList
                members = membersList
                memberBalances =
                    expenseCalculator.calculateMemberBalances(expensesList, membersList)

                // RESTORED: Calculate settlements with status
                val calculatedSettlements =
                    expenseCalculator.calculateSettlements(expensesList, membersList)
                val existingRecords = settlementRepository.getSettlementsForCollection(collectionId)

                settlements = combineSettlementsWithRecords(calculatedSettlements, existingRecords)

                // If no settlement records exist and we have settlements to create, save them
                if (existingRecords.isEmpty() && calculatedSettlements.isNotEmpty()) {
                    settlementRepository.saveSettlementsFromCalculation(
                        collectionId,
                        calculatedSettlements
                    ).fold(
                        onSuccess = { newRecords ->
                            settlements =
                                combineSettlementsWithRecords(calculatedSettlements, newRecords)
                        },
                        onFailure = { exception ->
                            println("DEBUG Settlement: Failed to save settlements: ${exception.message}")
                        }
                    )
                }

                println("DEBUG Settlement: Loaded ${expensesList.size} expenses, ${membersList.size} members")
                println("DEBUG Settlement: Generated ${settlements.size} settlements (${settlements.count { it.isSettled }} settled)")

            } catch (e: Exception) {
                errorMessage = "Error loading settlement data: ${e.message}"
                println("DEBUG Settlement error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // RESTORED: Combine calculated settlements with persisted records
    private fun combineSettlementsWithRecords(
        calculatedSettlements: List<Settlement>,
        records: List<SettlementRecord>
    ): List<SettlementWithStatus> {
        return calculatedSettlements.mapNotNull { settlement ->
            val matchingRecord = records.find { record ->
                record.fromMemberId == settlement.fromMember.id &&
                        record.toMemberId == settlement.toMember.id &&
                        Math.abs(record.amount - settlement.amount) < 0.01
            }

            SettlementWithStatus(
                settlement = settlement,
                settlementRecord = matchingRecord,
                isSettled = matchingRecord?.isSettled ?: false
            )
        }
    }

    // RESTORED: Mark settlement as settled functionality
    fun markSettlementAsSettled(settlementWithStatus: SettlementWithStatus) {
        val record = settlementWithStatus.settlementRecord
        if (record == null) {
            errorMessage = "Settlement record not found"
            return
        }

        viewModelScope.launch {
            try {
                settlementRepository.markAsSettled(record.id, true).fold(
                    onSuccess = {
                        // Update local state
                        settlements = settlements.map { item ->
                            if (item.settlementRecord?.id == record.id) {
                                item.copy(isSettled = true)
                            } else {
                                item
                            }
                        }
                        println("DEBUG Settlement: Marked settlement ${record.id} as settled")
                    },
                    onFailure = { exception ->
                        errorMessage = "Failed to mark settlement as settled: ${exception.message}"
                        println("DEBUG Settlement: Error marking as settled: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                errorMessage = "Error marking settlement as settled: ${e.message}"
                println("DEBUG Settlement: Exception marking as settled: ${e.message}")
            }
        }
    }

    fun refreshData() {
        if (currentCollectionId != -1L) {
            loadSettlementData(currentCollectionId)
        }
    }

    fun refreshData(collectionId: Long) {
        currentCollectionId = collectionId
        loadSettlementData(collectionId)
    }

    fun getTotalCollectionAmount(): Double {
        return expenses.sumOf { it.amount }
    }

    fun clearErrorMessage() {
        errorMessage = ""
    }

    // RESTORED: Get only unsettled settlements for display
    fun getUnsettledSettlements(): List<SettlementWithStatus> {
        return settlements.filter { !it.isSettled }
    }

    // RESTORED: Get settled settlements count for summary
    fun getSettledCount(): Int {
        return settlements.count { it.isSettled }
    }

    // RESTORED: Check if all settlements are settled
    fun areAllSettled(): Boolean {
        return settlements.isNotEmpty() && settlements.all { it.isSettled }
    }
}
