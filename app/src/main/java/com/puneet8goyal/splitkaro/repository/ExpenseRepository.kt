package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.ExpenseDao
import com.puneet8goyal.splitkaro.data.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    suspend fun insertExpense(expense: Expense): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseDao.insertExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllExpenses(): List<Expense> = withContext(Dispatchers.IO) {
        try {
            expenseDao.getAllExpenses()
        } catch (e: Exception) {
            emptyList() // Return empty list on error
        }
    }

    suspend fun updateExpense(expense: Expense): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseDao.updateExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(expense: Expense): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseDao.deleteExpense(expense)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExpenseById(id: Long): Expense? = withContext(Dispatchers.IO) {
        try {
            expenseDao.getAllExpenses().find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteAllExpenses(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val allExpenses = expenseDao.getAllExpenses()
            allExpenses.forEach { expenseDao.deleteExpense(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}