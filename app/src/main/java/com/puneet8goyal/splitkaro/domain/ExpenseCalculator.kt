package com.puneet8goyal.splitkaro.domain

import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.Member

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

// NEW: Add this data class
data class UserCentricBalance(
    val member: Member,
    val amountOwedToUser: Double, // positive = they owe current user, negative = current user owes them
    val isPositive: Boolean
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

    // NEW: Calculate what each other member owes to the current user (excludes current user from results)
    fun calculateUserCentricBalances(
        expenses: List<Expense>,
        members: List<Member>,
        currentUserId: Long
    ): List<UserCentricBalance> {
        val userCentricBalances = mutableListOf<UserCentricBalance>()

        // Get other members (excluding current user)
        val otherMembers = members.filter { it.id != currentUserId }

        otherMembers.forEach { otherMember ->
            var amountOwedToUser = 0.0

            // Check all expenses involving both users
            expenses.forEach { expense ->
                when {
                    // Current user paid, other member is in split
                    expense.paidByMemberId == currentUserId && otherMember.id in expense.splitAmongMemberIds -> {
                        amountOwedToUser += expense.perPersonAmount
                    }
                    // Other member paid, current user is in split
                    expense.paidByMemberId == otherMember.id && currentUserId in expense.splitAmongMemberIds -> {
                        amountOwedToUser -= expense.perPersonAmount
                    }
                }
            }

            // Only include if there's an actual balance (avoid zero amounts)
            if (kotlin.math.abs(amountOwedToUser) > 0.01) {
                userCentricBalances.add(
                    UserCentricBalance(
                        member = otherMember,
                        amountOwedToUser = amountOwedToUser,
                        isPositive = amountOwedToUser > 0
                    )
                )
            }
        }

        return userCentricBalances.sortedByDescending { it.amountOwedToUser }
    }

    // NEW: Calculate overall balance for current user
    fun calculateCurrentUserOverallBalance(
        expenses: List<Expense>,
        members: List<Member>,
        currentUserId: Long
    ): Double {
        val userCentricBalances = calculateUserCentricBalances(expenses, members, currentUserId)
        return userCentricBalances.sumOf { it.amountOwedToUser }
    }
}
