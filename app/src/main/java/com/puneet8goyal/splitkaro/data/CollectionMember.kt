package com.puneet8goyal.splitkaro.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "collection_members",
    primaryKeys = ["collectionId", "memberId"],
    foreignKeys = [
        ForeignKey(
            entity = ExpenseCollection::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CollectionMember(
    val collectionId: Long,
    val memberId: Long
)