package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.SettlementDao
import com.puneet8goyal.splitkaro.data.SettlementRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettlementRepository @Inject constructor(
    private val settlementDao: SettlementDao
) {
    suspend fun insertSettlement(settlement: SettlementRecord): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                settlementDao.insertSettlement(settlement)
                Result.success(Unit)
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

    suspend fun saveSettlementsFromCalculation(
        collectionId: Long,
        settlements: List<com.puneet8goyal.splitkaro.domain.Settlement>
    ): Result<List<SettlementRecord>> = withContext(Dispatchers.IO) {
        try {
            // Clear existing unsettled settlements
            clearSettlements(collectionId)

            // Save new settlements
            val settlementRecords = settlements.map { settlement ->
                SettlementRecord(
                    collectionId = collectionId,
                    fromMemberId = settlement.fromMember.id,
                    toMemberId = settlement.toMember.id,
                    amount = settlement.amount,
                    isSettled = false,
                    settledAt = null
                )
            }

            settlementRecords.forEach { record ->
                settlementDao.insertSettlement(record)
            }

            println("DEBUG SettlementRepository: Saved ${settlementRecords.size} new settlement records")
            Result.success(settlementRecords)
        } catch (e: Exception) {
            println("DEBUG SettlementRepository: Error saving settlements: ${e.message}")
            Result.failure(e)
        }
    }
}
