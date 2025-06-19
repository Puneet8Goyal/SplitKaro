package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.ExpenseDao
import com.puneet8goyal.splitkaro.data.Expense
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    suspend fun insertExpense(expense: Expense){
        expenseDao.insertExpense(expense)
    }

    suspend fun getExpensesForGroup(groupId:Long):List<Expense>{
        return expenseDao.getExpenseFromGroup(groupId)
    }
}