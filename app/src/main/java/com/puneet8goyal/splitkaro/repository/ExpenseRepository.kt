package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.ExpenseDao
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.repository.interfaces.ExpenseRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepositoryInterface {

    override suspend fun insertExpense(expense: Expense): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                println("DEBUG: Inserting expense: ${expense.description}")
                val id = expenseDao.insertExpense(expense)
                println("DEBUG: Expense inserted successfully with ID: $id")
                Result.success(id)
            } catch (e: Exception) {
                println("DEBUG: Error inserting expense: ${e.message}")
                Result.failure(e)
            }
        }

    override suspend fun getAllExpenses(): List<Expense> = withContext(Dispatchers.IO) {
        try {
            val expenses = expenseDao.getAllExpenses()
            println("DEBUG: Fetched ${expenses.size} expenses")
            expenses
        } catch (e: Exception) {
            println("DEBUG: Error fetching expenses: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getExpensesByCollectionId(collectionId: Long): List<Expense> =
        withContext(Dispatchers.IO) {
            try {
                val expenses = expenseDao.getExpensesByCollectionId(collectionId)
                println("DEBUG: Fetched ${expenses.size} expenses for collection $collectionId")
                expenses
            } catch (e: Exception) {
                println("DEBUG: Error fetching expenses: ${e.message}")
                emptyList()
            }
        }

    // NEW: Reactive data with Flow
    override fun getExpensesFlowByCollectionId(collectionId: Long): Flow<List<Expense>> {
        return expenseDao.getExpensesFlowByCollectionId(collectionId)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getExpenseById(id: Long): Expense? = withContext(Dispatchers.IO) {
        try {
            expenseDao.getExpenseById(id)
        } catch (e: Exception) {
            println("DEBUG: Error fetching expense by ID: ${e.message}")
            null
        }
    }

    override suspend fun updateExpense(expense: Expense): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                expenseDao.updateExpense(expense)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteExpense(expense: Expense): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                expenseDao.deleteExpense(expense)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteAllExpenses(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            expenseDao.deleteAllExpenses()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteExpensesByCollectionId(collectionId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                expenseDao.deleteExpensesByCollectionId(collectionId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
