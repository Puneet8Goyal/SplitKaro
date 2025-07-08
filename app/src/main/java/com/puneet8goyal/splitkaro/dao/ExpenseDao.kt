package com.puneet8goyal.splitkaro.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.puneet8goyal.splitkaro.data.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses WHERE collectionId = :collectionId ORDER BY createdAt DESC")
    suspend fun getExpensesByCollectionId(collectionId: Long): List<Expense>

    // NEW: Reactive Flow version
    @Query("SELECT * FROM expenses WHERE collectionId = :collectionId ORDER BY createdAt DESC")
    fun getExpensesFlowByCollectionId(collectionId: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): Expense?

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()

    @Query("DELETE FROM expenses WHERE collectionId = :collectionId")
    suspend fun deleteExpensesByCollectionId(collectionId: Long)
}
