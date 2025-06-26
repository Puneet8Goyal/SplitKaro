package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.GroupDao
import com.puneet8goyal.splitkaro.data.Group
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {
    suspend fun insertGroup(group: Group): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            groupDao.insertGroup(group)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllGroups(): List<Group> = withContext(Dispatchers.IO) {
        try {
            groupDao.getAllGroups()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateGroup(group: Group): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            groupDao.updateGroup(group)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGroup(group: Group): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            groupDao.deleteGroup(group)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}