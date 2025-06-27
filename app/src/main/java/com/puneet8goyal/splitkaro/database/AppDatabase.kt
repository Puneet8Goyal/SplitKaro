package com.puneet8goyal.splitkaro.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.puneet8goyal.splitkaro.dao.ExpenseCollectionDao
import com.puneet8goyal.splitkaro.dao.ExpenseDao
import com.puneet8goyal.splitkaro.dao.MemberDao
import com.puneet8goyal.splitkaro.dao.SettlementDao
import com.puneet8goyal.splitkaro.data.CollectionMember
import com.puneet8goyal.splitkaro.data.Expense
import com.puneet8goyal.splitkaro.data.ExpenseCollection
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.data.SettlementRecord

@Database(
    entities = [Expense::class, ExpenseCollection::class, Member::class, CollectionMember::class, SettlementRecord::class],
    version = 1, // Incremented for removing category
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCollectionDao(): ExpenseCollectionDao
    abstract fun memberDao(): MemberDao
    abstract fun settlementDao(): SettlementDao
}