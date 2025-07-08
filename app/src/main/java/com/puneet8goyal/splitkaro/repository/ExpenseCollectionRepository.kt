package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.ExpenseCollectionDao
import com.puneet8goyal.splitkaro.data.ExpenseCollection
import com.puneet8goyal.splitkaro.repository.interfaces.ExpenseCollectionRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseCollectionRepository @Inject constructor(
    private val expenseCollectionDao: ExpenseCollectionDao
) : ExpenseCollectionRepositoryInterface {

    override suspend fun insertCollection(expenseCollection: ExpenseCollection): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val collectionId = expenseCollectionDao.insertCollection(expenseCollection)
                Result.success(collectionId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getAllCollections(): List<ExpenseCollection> =
        withContext(Dispatchers.IO) {
            try {
                expenseCollectionDao.getAllCollections()
            } catch (e: Exception) {
                emptyList()
            }
        }

    override fun getCollectionsFlow(): Flow<List<ExpenseCollection>> {
        return expenseCollectionDao.getCollectionsFlow()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getCollectionById(id: Long): ExpenseCollection? =
        withContext(Dispatchers.IO) {
            try {
                expenseCollectionDao.getCollectionById(id)
            } catch (e: Exception) {
                println("DEBUG: Error getting collection by ID: ${e.message}")
                null
            }
        }

    override suspend fun updateCollection(expenseCollection: ExpenseCollection): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                expenseCollectionDao.updateCollection(expenseCollection)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun deleteCollection(expenseCollection: ExpenseCollection): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                expenseCollectionDao.deleteCollection(expenseCollection)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
