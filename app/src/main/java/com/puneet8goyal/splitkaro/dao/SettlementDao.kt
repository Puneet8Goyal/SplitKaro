package com.puneet8goyal.splitkaro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puneet8goyal.splitkaro.data.SettlementRecord

@Dao
interface SettlementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementRecord): Long // FIXED: Return Long

    @Query("SELECT * FROM settlements WHERE collectionId = :collectionId")
    suspend fun getSettlementsForCollection(collectionId: Long): List<SettlementRecord> // FIXED: Correct return type

    @Query("UPDATE settlements SET isSettled = :isSettled, settledAt = :settledAt WHERE id = :settlementId")
    suspend fun markAsSettled(
        settlementId: Long,
        isSettled: Boolean,
        settledAt: Long?
    ) // FIXED: Return Unit

    @Query("DELETE FROM settlements WHERE collectionId = :collectionId AND isSettled = 0") // FIXED: Only delete unsettled
    suspend fun clearSettlements(collectionId: Long) // FIXED: Return Unit

    @Query("SELECT * FROM settlements WHERE id = :settlementId")
    suspend fun getSettlementById(settlementId: Long): SettlementRecord? // FIXED: Correct return type
}
