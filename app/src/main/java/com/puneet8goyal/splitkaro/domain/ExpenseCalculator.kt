package com.puneet8goyal.splitkaro.domain

import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.OwedResult

class ExpenseCalculator {
    fun calculatePerPersonAmount(totalAmount: Double, splitAmong: Int): Double {
        return if (splitAmong > 0) totalAmount / splitAmong else 0.0
    }

    fun calculateOwed(expense: Expense): List<OwedResult> {
        val perPerson = calculatePerPersonAmount(expense.amount, expense.splitAmong)
        val owedList = mutableListOf<OwedResult>()
        owedList.add(OwedResult(expense.paidBy, -perPerson * (expense.splitAmong - 1)))
        for (i in 1 until expense.splitAmong) {
            val person = "Person$i"
            if (person != expense.paidBy) owedList.add(OwedResult(person, perPerson))
        }
        return owedList
    }

    fun calculateOverallBalance(expenses: List<Expense>): Double {
        return expenses.sumOf { expense ->
            val perPerson = calculatePerPersonAmount(expense.amount, expense.splitAmong)
            expense.amount - (perPerson * (expense.splitAmong - 1))
        }
    }
}