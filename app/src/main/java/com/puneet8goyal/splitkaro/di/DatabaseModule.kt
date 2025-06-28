package com.puneet8goyal.splitkaro.di

import android.content.Context
import androidx.room.Room
import com.puneet8goyal.splitkaro.dao.ExpenseCollectionDao
import com.puneet8goyal.splitkaro.dao.ExpenseDao
import com.puneet8goyal.splitkaro.dao.MemberDao
import com.puneet8goyal.splitkaro.dao.SettlementDao
import com.puneet8goyal.splitkaro.database.AppDatabase
import com.puneet8goyal.splitkaro.domain.ExpenseCalculator
import com.puneet8goyal.splitkaro.utils.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "splitkaro_database"
        ).build()
    }

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideMemberDao(database: AppDatabase): MemberDao = database.memberDao()

    @Provides
    fun provideExpenseCollectionDao(database: AppDatabase): ExpenseCollectionDao = database.expenseCollectionDao()

    @Provides
    fun provideSettlementDao(database: AppDatabase): SettlementDao = database.settlementDao()

    @Provides
    @Singleton
    fun provideExpenseCalculator(): ExpenseCalculator = ExpenseCalculator()

    // NEW: Provide UserPreferences
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
