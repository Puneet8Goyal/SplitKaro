package com.puneet8goyal.splitkaro.repository

import com.puneet8goyal.splitkaro.dao.MemberDao
import com.puneet8goyal.splitkaro.data.CollectionMember
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.repository.interfaces.MemberRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val memberDao: MemberDao
) : MemberRepositoryInterface {

    override suspend fun insertMember(member: Member): Result<Long> = withContext(Dispatchers.IO) {
        try {
            println("DEBUG: Inserting member: ${member.name}")
            val memberId = memberDao.insertMember(member)
            println("DEBUG: Member inserted with ID: $memberId")
            Result.success(memberId)
        } catch (e: Exception) {
            println("DEBUG: Error inserting member: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun insertCollectionMember(collectionMember: CollectionMember): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val exists = memberDao.checkMemberInCollection(
                    collectionMember.memberId,
                    collectionMember.collectionId
                ) > 0

                if (exists) {
                    println("DEBUG: Member ${collectionMember.memberId} already in collection ${collectionMember.collectionId}")
                    return@withContext Result.success(Unit)
                }

                println("DEBUG: Adding member ${collectionMember.memberId} to collection ${collectionMember.collectionId}")
                memberDao.insertCollectionMember(collectionMember)
                Result.success(Unit)
            } catch (e: Exception) {
                println("DEBUG: Error adding member to collection: ${e.message}")
                Result.failure(e)
            }
        }

    override suspend fun getAllMembers(): List<Member> = withContext(Dispatchers.IO) {
        try {
            val members = memberDao.getAllMembers()
            println("DEBUG: Fetched ${members.size} members")
            members
        } catch (e: Exception) {
            println("DEBUG: Error fetching members: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getMembersByCollectionId(collectionId: Long): List<Member> =
        withContext(Dispatchers.IO) {
            try {
                val members = memberDao.getMembersByCollectionId(collectionId)
                println("DEBUG: Fetched ${members.size} members for collection $collectionId")
                members
            } catch (e: Exception) {
                println("DEBUG: Error fetching members for collection: ${e.message}")
                emptyList()
            }
        }

    // NEW: Reactive data with Flow
    override fun getMembersFlowByCollectionId(collectionId: Long): Flow<List<Member>> {
        return memberDao.getMembersFlowByCollectionId(collectionId)
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getMemberById(id: Long): Member? = withContext(Dispatchers.IO) {
        try {
            memberDao.getMemberById(id)
        } catch (e: Exception) {
            println("DEBUG: Error fetching member by ID: ${e.message}")
            null
        }
    }

    override suspend fun updateMember(member: Member): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            memberDao.updateMember(member)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMember(member: Member): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            memberDao.removeAllCollectionMemberships(member.id)
            memberDao.deleteMember(member)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeMemberFromCollection(
        collectionId: Long,
        memberId: Long
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                memberDao.removeMemberFromCollection(collectionId, memberId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun isMemberInCollection(memberId: Long, collectionId: Long): Boolean =
        withContext(Dispatchers.IO) {
            try {
                memberDao.checkMemberInCollection(memberId, collectionId) > 0
            } catch (e: Exception) {
                false
            }
        }
}
