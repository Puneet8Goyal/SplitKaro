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
            println("DEBUG: Inserting expense: ${expense.description} for collectionId: ${expense.collectionId}")
            expenseDao.insertExpense(expense)
            println("DEBUG: Expense inserted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            println("DEBUG: Error inserting expense: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAllExpenses(): List<Expense> = withContext(Dispatchers.IO) {
        try {
            val expenses = expenseDao.getAllExpenses()
            println("DEBUG: Fetched ${expenses.size} expenses")
            expenses
        } catch (e: Exception) {
            println("DEBUG: Error fetching expenses: ${e.message}")
            emptyList()
        }
    }

    suspend fun getExpensesByCollectionId(collectionId: Long): List<Expense> =
        withContext(Dispatchers.IO) {
            try {
                val expenses = expenseDao.getExpensesByCollectionId(collectionId)
                println("DEBUG: Fetched ${expenses.size} expenses for collectionId: $collectionId")
                expenses
            } catch (e: Exception) {
                println("DEBUG: Error fetching expenses: ${e.message}")
                emptyList()
            }
        }

    suspend fun getExpenseById(id: Long): Expense? = withContext(Dispatchers.IO) {
        try {
            expenseDao.getExpenseById(id)
        } catch (e: Exception) {
            null
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

    suspend fun deleteAllExpenses(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseDao.deleteAllExpenses()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpensesByCollectionId(collectionId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                expenseDao.deleteExpensesByCollectionId(collectionId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}