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
    private val settlementRepository: SettlementRepository,
    private val expenseCalculator: ExpenseCalculator
) : ViewModel() {

    var expenses by mutableStateOf<List<Expense>>(emptyList())
    var members by mutableStateOf<List<Member>>(emptyList())
    var memberBalances by mutableStateOf<List<MemberBalance>>(emptyList())
    var settlements by mutableStateOf<List<SettlementWithStatus>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")

    private var currentCollectionId: Long = -1L

    data class SettlementWithStatus(
        val settlement: Settlement,
        val settlementRecord: SettlementRecord? = null,
        val isSettled: Boolean = false
    )

    fun loadSettlementData(collectionId: Long) {
        currentCollectionId = collectionId
        isLoading = true
        errorMessage = ""
        successMessage = ""

        viewModelScope.launch {
            try {
                // Load expenses and members for this specific collection only
                val expensesList = expenseRepository.getExpensesByCollectionId(collectionId)
                val membersList = memberRepository.getMembersByCollectionId(collectionId)

                expenses = expensesList
                members = membersList

                // Calculate member balances for this collection only
                memberBalances =
                    expenseCalculator.calculateMemberBalances(expensesList, membersList)

                // Calculate direct settlements without debt simplification
                val calculatedSettlements = calculateDirectSettlements(memberBalances)

                // Load existing settlement records for this collection only
                val existingRecords = settlementRepository.getSettlementsForCollection(collectionId)

                // Combine calculated settlements with existing records
                settlements = combineSettlementsWithRecords(calculatedSettlements, existingRecords)

                println("DEBUG Settlement: Loaded ${expensesList.size} expenses, ${membersList.size} members for collection $collectionId")
                println("DEBUG Settlement: Generated ${settlements.size} settlements (${settlements.count { it.isSettled }} settled)")

            } catch (e: Exception) {
                errorMessage = "Error loading settlement data: ${e.message}"
                println("DEBUG Settlement error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // Direct settlements to prevent cross-effects
    private fun calculateDirectSettlements(balances: List<MemberBalance>): List<Settlement> {
        val settlements = mutableListOf<Settlement>()

        // Get debtors and creditors
        val debtors = balances.filter { it.netBalance < -0.01 }
        val creditors = balances.filter { it.netBalance > 0.01 }

        // Create direct settlements between each debtor and creditor
        debtors.forEach { debtor ->
            var remainingDebt = -debtor.netBalance

            creditors.forEach { creditor ->
                if (remainingDebt > 0.01 && creditor.netBalance > 0.01) {
                    val settlementAmount = minOf(remainingDebt, creditor.netBalance)

                    settlements.add(
                        Settlement(
                            fromMember = debtor.member,
                            toMember = creditor.member,
                            amount = settlementAmount
                        )
                    )

                    remainingDebt -= settlementAmount
                }
            }
        }

        return settlements.filter { it.amount > 0.01 }
    }

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

    // FIXED: Mark settlement and create actual settlement expense with success callback
    fun markSettlementAsSettled(settlementWithStatus: SettlementWithStatus, onSuccess: () -> Unit) {
        val settlement = settlementWithStatus.settlement
        val record = settlementWithStatus.settlementRecord

        viewModelScope.launch {
            try {
                successMessage = ""
                errorMessage = ""

                // Step 1: Create or update settlement record
                val recordId = if (record != null) {
                    settlementRepository.markAsSettled(record.id, true).fold(
                        onSuccess = { record.id },
                        onFailure = { throw it }
                    )
                } else {
                    // Create new settlement record
                    val newRecord = SettlementRecord(
                        collectionId = currentCollectionId,
                        fromMemberId = settlement.fromMember.id,
                        toMemberId = settlement.toMember.id,
                        amount = settlement.amount,
                        isSettled = true,
                        settledAt = System.currentTimeMillis()
                    )
                    settlementRepository.insertSettlement(newRecord).fold(
                        onSuccess = { it },
                        onFailure = { throw it }
                    )
                }

                // Step 2: Create settlement expense for history tracking
                val settlementExpense = Expense(
                    collectionId = currentCollectionId,
                    description = "Settlement: ${settlement.fromMember.name} → ${settlement.toMember.name}",
                    amount = settlement.amount,
                    paidByMemberId = settlement.fromMember.id,
                    splitAmongMemberIds = listOf(settlement.toMember.id),
                    perPersonAmount = settlement.amount,
                    createdAt = System.currentTimeMillis()
                )

                expenseRepository.insertExpense(settlementExpense).fold(
                    onSuccess = {
                        // Update local state
                        settlements = settlements.map { item ->
                            if (item.settlement.fromMember.id == settlement.fromMember.id &&
                                item.settlement.toMember.id == settlement.toMember.id
                            ) {
                                item.copy(isSettled = true)
                            } else {
                                item
                            }
                        }

                        // Set success message
                        successMessage = "💰 Settlement completed successfully!"

                        // Call success callback
                        onSuccess()

                        // Reload data to refresh everything
                        loadSettlementData(currentCollectionId)

                        println("DEBUG Settlement: Successfully settled ${settlement.amount} from ${settlement.fromMember.name} to ${settlement.toMember.name}")
                    },
                    onFailure = { exception ->
                        errorMessage = "Failed to record settlement expense: ${exception.message}"
                        println("DEBUG Settlement: Error recording expense: ${exception.message}")
                    }
                )

            } catch (e: Exception) {
                errorMessage = "Error settling payment: ${e.message}"
                println("DEBUG Settlement: Exception settling payment: ${e.message}")
            }
        }
    }

    fun refreshData(collectionId: Long) {
        isLoading = true
        currentCollectionId = collectionId
        loadSettlementData(collectionId)
    }


    fun getTotalCollectionAmount(): Double {
        return expenses.sumOf { it.amount }
    }

    fun clearErrorMessage() {
        errorMessage = ""
    }

    fun clearSuccessMessage() {
        successMessage = ""
    }

    fun getUnsettledSettlements(): List<SettlementWithStatus> {
        return settlements.filter { !it.isSettled }
    }

    fun getSettledCount(): Int {
        return settlements.count { it.isSettled }
    }

    fun areAllSettled(): Boolean {
        return settlements.isNotEmpty() && settlements.all { it.isSettled }
    }
}
