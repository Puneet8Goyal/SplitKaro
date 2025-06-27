package com.puneet8goyal.splitkaro.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

// Simple settlement tracking
@Entity(
    tableName = "settlements",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseCollection::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SettlementRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val collectionId: Long,
    val fromMemberId: Long,
    val toMemberId: Long,
    val amount: Double,
    val isSettled: Boolean = false,
    val settledAt: Long? = null
)

// Enhanced Settlement with status
data class SettlementWithStatus(
    val settlement: com.puneet8goyal.splitkaro.domain.Settlement,
    val isSettled: Boolean = false,
    val settledAt: Long? = null
)