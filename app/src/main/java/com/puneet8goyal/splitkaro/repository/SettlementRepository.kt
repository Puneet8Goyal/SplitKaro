package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.SettlementDao
import com.puneet8goyal.splitkaro.data.SettlementRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettlementRepository @Inject constructor(
    private val settlementDao: SettlementDao
) {

    suspend fun insertSettlement(settlement: SettlementRecord): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val id = settlementDao.insertSettlement(settlement)
                println("DEBUG SettlementRepository: Inserted settlement record with ID: $id")
                Result.success(id)
            } catch (e: Exception) {
                println("DEBUG SettlementRepository: Error inserting settlement: ${e.message}")
                Result.failure(e)
            }
        }

    suspend fun getSettlementsForCollection(collectionId: Long): List<SettlementRecord> =
        withContext(Dispatchers.IO) {
            try {
                val settlements = settlementDao.getSettlementsForCollection(collectionId)
                println("DEBUG SettlementRepository: Loaded ${settlements.size} settlement records for collection $collectionId")
                settlements
            } catch (e: Exception) {
                println("DEBUG SettlementRepository: Error loading settlements: ${e.message}")
                emptyList()
            }
        }

    suspend fun markAsSettled(settlementId: Long, isSettled: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val settledAt = if (isSettled) System.currentTimeMillis() else null
                settlementDao.markAsSettled(settlementId, isSettled, settledAt)
                println("DEBUG SettlementRepository: Marked settlement $settlementId as settled: $isSettled")
                Result.success(Unit)
            } catch (e: Exception) {
                println("DEBUG SettlementRepository: Error marking settlement as settled: ${e.message}")
                Result.failure(e)
            }
        }

    suspend fun clearSettlements(collectionId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                settlementDao.clearSettlements(collectionId)
                println("DEBUG SettlementRepository: Cleared settlements for collection $collectionId")
                Result.success(Unit)
            } catch (e: Exception) {
                println("DEBUG SettlementRepository: Error clearing settlements: ${e.message}")
                Result.failure(e)
            }
        }
}
