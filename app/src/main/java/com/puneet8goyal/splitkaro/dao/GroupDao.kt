package com.puneet8goyal.splitkaro.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.puneet8goyal.splitkaro.data.Group

@Dao
interface GroupDao {
    @Insert
    suspend fun insertGroup(group: Group)

    @Query("SELECT * FROM groups")
    suspend fun getAllGroups(): List<Group>

    @Update
    suspend fun updateGroup(group:Group)

    @Delete
    suspend fun deleteGroup(group: Group)
}