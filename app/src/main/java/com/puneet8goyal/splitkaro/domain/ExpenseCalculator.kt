package com.puneet8goyal.splitkaro.domain

import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.data.OwedResult

data class ExpenseSummary(
    val totalExpenses: Int,
    val totalAmount: Double,
    val userShare: Double,
    val overallBalance: Double,
    val averageExpense: Double
)

data class MemberBalance(
    val member: Member,
    val totalPaid: Double,
    val totalOwed: Double,
    val netBalance: Double // positive = owed money, negative = owes money
)

data class Settlement(
    val fromMember: Member,
    val toMember: Member,
    val amount: Double
)

class ExpenseCalculator {

    fun calculateExpenseSummary(expenses: List<Expense>, userId: Long? = null): ExpenseSummary {
        val totalExpenses = expenses.size
        val totalAmount = expenses.sumOf { it.amount }
        val averageExpense = if (totalExpenses > 0) totalAmount / totalExpenses else 0.0

        // Calculate user-specific data if userId provided
        val userShare = if (userId != null) {
            expenses.filter { userId in it.splitAmongMemberIds }.sumOf { it.perPersonAmount }
        } else 0.0

        val userPaid = if (userId != null) {
            expenses.filter { it.paidByMemberId == userId }.sumOf { it.amount }
        } else 0.0

        val overallBalance = userPaid - userShare

        return ExpenseSummary(
            totalExpenses = totalExpenses,
            totalAmount = totalAmount,
            userShare = userShare,
            overallBalance = overallBalance,
            averageExpense = averageExpense
        )
    }

    fun getExpensesByPayer(expenses: List<Expense>): Map<Long, List<Expense>> {
        return expenses.groupBy { it.paidByMemberId }
    }

    fun calculateMemberBalances(
        expenses: List<Expense>,
        members: List<Member>
    ): List<MemberBalance> {
        return members.map { member ->
            val totalPaid = expenses.filter { it.paidByMemberId == member.id }.sumOf { it.amount }
            val totalOwed =
                expenses.filter { member.id in it.splitAmongMemberIds }.sumOf { it.perPersonAmount }
            val netBalance = totalPaid - totalOwed

            MemberBalance(
                member = member,
                totalPaid = totalPaid,
                totalOwed = totalOwed,
                netBalance = netBalance
            )
        }
    }

    fun calculateSettlements(expenses: List<Expense>, members: List<Member>): List<Settlement> {
        val balances = calculateMemberBalances(expenses, members)
        val settlements = mutableListOf<Settlement>()

        // Separate creditors (positive balance) and debtors (negative balance)
        val creditors = balances.filter { it.netBalance > 0 }.sortedByDescending { it.netBalance }
            .toMutableList()
        val debtors =
            balances.filter { it.netBalance < 0 }.sortedBy { it.netBalance }.toMutableList()

        var i = 0
        var j = 0

        while (i < creditors.size && j < debtors.size) {
            val creditor = creditors[i]
            val debtor = debtors[j]

            val amount = minOf(creditor.netBalance, -debtor.netBalance)

            if (amount > 0.01) { // Avoid tiny settlements
                settlements.add(
                    Settlement(
                        fromMember = debtor.member,
                        toMember = creditor.member,
                        amount = amount
                    )
                )

                // Update balances
                creditors[i] = creditor.copy(netBalance = creditor.netBalance - amount)
                debtors[j] = debtor.copy(netBalance = debtor.netBalance + amount)
            }

            // Move to next creditor/debtor if balance is settled
            if (creditors[i].netBalance < 0.01) i++
            if (debtors[j].netBalance > -0.01) j++
        }

        return settlements
    }

    fun calculateWhoOwesWhom(expenses: List<Expense>, members: List<Member>): List<OwedResult> {
        val balances = calculateMemberBalances(expenses, members)
        return balances.map { balance ->
            OwedResult(
                person = balance.member.name,
                amount = balance.netBalance
            )
        }
    }

    fun calculateSettlementsWithStatus(
        expenses: List<Expense>,
        members: List<Member>,
        settledRecords: List<com.puneet8goyal.splitkaro.data.SettlementRecord> = emptyList()
    ): List<com.puneet8goyal.splitkaro.data.SettlementWithStatus> {
        val settlements = calculateSettlements(expenses, members)

        return settlements.map { settlement ->
            val settledRecord = settledRecords.find { record ->
                record.fromMemberId == settlement.fromMember.id &&
                        record.toMemberId == settlement.toMember.id &&
                        record.amount == settlement.amount
            }

            com.puneet8goyal.splitkaro.data.SettlementWithStatus(
                settlement = settlement,
                isSettled = settledRecord?.isSettled ?: false,
                settledAt = settledRecord?.settledAt
            )
        }
    }

    fun getMemberExpenseBreakdown(expenses: List<Expense>, memberId: Long): Pair<Double, Double> {
        val totalPaid = expenses.filter { it.paidByMemberId == memberId }.sumOf { it.amount }
        val totalOwed =
            expenses.filter { memberId in it.splitAmongMemberIds }.sumOf { it.perPersonAmount }
        return Pair(totalPaid, totalOwed)
    }
}