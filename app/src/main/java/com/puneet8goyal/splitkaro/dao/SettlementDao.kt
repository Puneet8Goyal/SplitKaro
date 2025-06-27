package com.puneet8goyal.splitkaro.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.puneet8goyal.splitkaro.data.SettlementRecord

@Dao
interface SettlementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementRecord)

    @Query("SELECT * FROM settlements WHERE collectionId = :collectionId")
    suspend fun getSettlementsForCollection(collectionId: Long): List<SettlementRecord>

    @Query("UPDATE settlements SET isSettled = :isSettled, settledAt = :settledAt WHERE id = :settlementId")
    suspend fun markAsSettled(settlementId: Long, isSettled: Boolean, settledAt: Long?)

    @Query("DELETE FROM settlements WHERE collectionId = :collectionId")
    suspend fun clearSettlements(collectionId: Long)
}