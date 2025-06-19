package com.puneet8goyal.splitkaro.di

import android.content.Context
import androidx.room.Room
import com.puneet8goyal.splitkaro.database.AppDatabase
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideExpenseDao(database: AppDatabase) = database.expenseDao()
}