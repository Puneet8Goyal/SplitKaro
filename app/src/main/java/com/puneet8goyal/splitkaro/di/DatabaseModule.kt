package com.puneet8goyal.splitkaro.di

import android.content.Context
import androidx.room.Room
import com.puneet8goyal.splitkaro.database.AppDatabase
import com.puneet8goyal.splitkaro.domain.ExpenseCalculator
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "splitkaro_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: AppDatabase) = database.expenseDao()

    @Provides
    @Singleton
    fun provideExpenseCollectionDao(database: AppDatabase) = database.expenseCollectionDao()

    @Provides
    @Singleton
    fun provideMemberDao(database: AppDatabase) = database.memberDao()

    @Provides
    @Singleton
    fun provideSettlementDao(database: AppDatabase) = database.settlementDao()

    @Provides
    @Singleton
    fun provideExpenseCalculator(): ExpenseCalculator {
        return ExpenseCalculator()
    }
}
