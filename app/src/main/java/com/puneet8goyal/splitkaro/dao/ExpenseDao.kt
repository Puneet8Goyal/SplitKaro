package com.puneet8goyal.splitkaro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.puneet8goyal.splitkaro.data.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE groupId = :groupId")
    suspend fun getExpenseFromGroup(groupId: Long): List<Expense>
}