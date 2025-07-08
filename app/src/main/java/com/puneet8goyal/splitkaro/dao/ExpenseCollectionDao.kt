package com.puneet8goyal.splitkaro.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.puneet8goyal.splitkaro.data.ExpenseCollection
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCollectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: ExpenseCollection): Long

    @Query("SELECT * FROM expense_collections ORDER BY createdAt DESC")
    suspend fun getAllCollections(): List<ExpenseCollection>

    // NEW: Reactive Flow version
    @Query("SELECT * FROM expense_collections ORDER BY createdAt DESC")
    fun getCollectionsFlow(): Flow<List<ExpenseCollection>>

    @Query("SELECT * FROM expense_collections WHERE id = :id")
    suspend fun getCollectionById(id: Long): ExpenseCollection?

    @Update
    suspend fun updateCollection(collection: ExpenseCollection)

    @Delete
    suspend fun deleteCollection(collection: ExpenseCollection)

    @Query("DELETE FROM expense_collections")
    suspend fun deleteAllCollections()
}
