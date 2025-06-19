package com.puneet8goyal.splitkaro.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.puneet8goyal.splitkaro.dao.ExpenseDao
import com.puneet8goyal.splitkaro.data.Expense

@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}