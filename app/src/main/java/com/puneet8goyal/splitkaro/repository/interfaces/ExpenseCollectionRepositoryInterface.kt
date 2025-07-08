package com.puneet8goyal.splitkaro.repository.interfaces

import com.puneet8goyal.splitkaro.data.ExpenseCollection
import kotlinx.coroutines.flow.Flow

interface ExpenseCollectionRepositoryInterface {
    suspend fun insertCollection(expenseCollection: ExpenseCollection): Result<Long>
    suspend fun getAllCollections(): List<ExpenseCollection>
    suspend fun getCollectionById(id: Long): ExpenseCollection?
    suspend fun updateCollection(expenseCollection: ExpenseCollection): Result<Unit>
    suspend fun deleteCollection(expenseCollection: ExpenseCollection): Result<Unit>

    // Reactive data with Flow
    fun getCollectionsFlow(): Flow<List<ExpenseCollection>>
}
