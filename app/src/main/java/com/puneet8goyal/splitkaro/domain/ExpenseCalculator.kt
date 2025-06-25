package com.puneet8goyal.splitkaro.domain

import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.OwedResult

class ExpenseCalculator {

    fun calculatePerPersonAmount(totalAmount: Double, splitAmong: Int): Double {
        return if (splitAmong > 0) totalAmount / splitAmong else 0.0
    }

    /**
     * Calculates what each person owes for a single expense.
     * Since we don't store individual participant names, this returns a simplified calculation.
     * The person who paid should receive: (totalAmount - perPersonShare)
     * Each other person owes: perPersonShare
     */
    fun calculateOwed(expense: Expense): List<OwedResult> {
        val perPerson = calculatePerPersonAmount(expense.amount, expense.splitAmong)
        val owedList = mutableListOf<OwedResult>()

        // The person who paid should receive money from others
        val amountPaidByPayer = perPerson
        val amountToReceive = expense.amount - amountPaidByPayer

        if (amountToReceive > 0) {
            owedList.add(OwedResult(expense.paidBy, -amountToReceive)) // Negative means they should receive
        }

        // For simplicity, we'll show how much each "other person" owes
        // Since we don't track individual names, we'll show the per-person amount
        if (expense.splitAmong > 1) {
            owedList.add(OwedResult("Others (${expense.splitAmong - 1} people)", perPerson))
        }

        return owedList
    }

    /**
     * Calculates the overall balance for the person who has been tracking expenses.
     * This shows how much money they should receive in total.
     */
    fun calculateOverallBalance(expenses: List<Expense>): Double {
        return expenses.sumOf { expense ->
            val perPerson = calculatePerPersonAmount(expense.amount, expense.splitAmong)
            // Amount the payer should receive from others
            expense.amount - perPerson
        }
    }

    /**
     * Calculates total amount spent by the user
     */
    fun calculateTotalSpent(expenses: List<Expense>): Double {
        return expenses.sumOf { it.amount }
    }

    /**
     * Calculates how much the user actually owes (their share of all expenses)
     */
    fun calculateUserShare(expenses: List<Expense>): Double {
        return expenses.sumOf { expense ->
            calculatePerPersonAmount(expense.amount, expense.splitAmong)
        }
    }

    /**
     * Groups expenses by the person who paid
     */
    fun getExpensesByPayer(expenses: List<Expense>): Map<String, List<Expense>> {
        return expenses.groupBy { it.paidBy }
    }

    /**
     * Calculates summary statistics for expenses
     */
    fun calculateExpenseSummary(expenses: List<Expense>): ExpenseSummary {
        val totalSpent = calculateTotalSpent(expenses)
        val userShare = calculateUserShare(expenses)
        val overallBalance = calculateOverallBalance(expenses)
        val averageExpense = if (expenses.isNotEmpty()) totalSpent / expenses.size else 0.0

        return ExpenseSummary(
            totalExpenses = expenses.size,
            totalAmount = totalSpent,
            userShare = userShare,
            overallBalance = overallBalance,
            averageExpense = averageExpense
        )
    }
}

data class ExpenseSummary(
    val totalExpenses: Int,
    val totalAmount: Double,
    val userShare: Double,
    val overallBalance: Double,
    val averageExpense: Double
)