package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.ExpenseCollectionDao
import com.puneet8goyal.splitkaro.data.ExpenseCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpenseCollectionRepository @Inject constructor(
    private val expenseCollectionDao: ExpenseCollectionDao
) {
    suspend fun insertCollection(expenseCollection: ExpenseCollection): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val collectionId = expenseCollectionDao.insertCollection(expenseCollection)
                Result.success(collectionId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun getAllCollections(): List<ExpenseCollection> = withContext(Dispatchers.IO) {
        try {
            expenseCollectionDao.getAllCollections()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCollectionById(id: Long): ExpenseCollection? = withContext(Dispatchers.IO) {
        try {
            expenseCollectionDao.getCollectionById(id)
        } catch (e: Exception) {
            println("DEBUG: Error getting collection by ID: ${e.message}")
            null
        }
    }

    suspend fun updateCollection(expenseCollection: ExpenseCollection): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                expenseCollectionDao.updateCollection(expenseCollection)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun deleteCollection(expenseCollection: ExpenseCollection): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                expenseCollectionDao.deleteCollection(expenseCollection)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}