package com.puneet8goyal.splitkaro.repository.interfaces

import com.puneet8goyal.splitkaro.data.CollectionMember
import com.puneet8goyal.splitkaro.data.Member
import kotlinx.coroutines.flow.Flow

interface MemberRepositoryInterface {
    suspend fun insertMember(member: Member): Result<Long>
    suspend fun insertCollectionMember(collectionMember: CollectionMember): Result<Unit>
    suspend fun getAllMembers(): List<Member>
    suspend fun getMembersByCollectionId(collectionId: Long): List<Member>
    suspend fun getMemberById(id: Long): Member?
    suspend fun updateMember(member: Member): Result<Unit>
    suspend fun deleteMember(member: Member): Result<Unit>
    suspend fun removeMemberFromCollection(collectionId: Long, memberId: Long): Result<Unit>
    suspend fun isMemberInCollection(memberId: Long, collectionId: Long): Boolean

    // Reactive data with Flow
    fun getMembersFlowByCollectionId(collectionId: Long): Flow<List<Member>>
}
