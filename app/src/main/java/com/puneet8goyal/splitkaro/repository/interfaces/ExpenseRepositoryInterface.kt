package com.puneet8goyal.splitkaro.repository.interfaces

import com.puneet8goyal.splitkaro.data.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepositoryInterface {
    suspend fun insertExpense(expense: Expense): Result<Long>
    suspend fun getAllExpenses(): List<Expense>
    suspend fun getExpensesByCollectionId(collectionId: Long): List<Expense>
    suspend fun getExpenseById(id: Long): Expense?
    suspend fun updateExpense(expense: Expense): Result<Unit>
    suspend fun deleteExpense(expense: Expense): Result<Unit>
    suspend fun deleteAllExpenses(): Result<Unit>
    suspend fun deleteExpensesByCollectionId(collectionId: Long): Result<Unit>

    // Reactive data with Flow
    fun getExpensesFlowByCollectionId(collectionId: Long): Flow<List<Expense>>
}
