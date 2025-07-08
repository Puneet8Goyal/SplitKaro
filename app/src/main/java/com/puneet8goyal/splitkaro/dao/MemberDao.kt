package com.puneet8goyal.splitkaro.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.puneet8goyal.splitkaro.data.CollectionMember
import com.puneet8goyal.splitkaro.data.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionMember(collectionMember: CollectionMember)

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("SELECT * FROM members")
    suspend fun getAllMembers(): List<Member>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Long): Member?

    @Query("SELECT m.* FROM members m INNER JOIN collection_members cm ON m.id = cm.memberId WHERE cm.collectionId = :collectionId ORDER BY m.name")
    suspend fun getMembersByCollectionId(collectionId: Long): List<Member>

    // NEW: Reactive Flow version
    @Query("SELECT m.* FROM members m INNER JOIN collection_members cm ON m.id = cm.memberId WHERE cm.collectionId = :collectionId ORDER BY m.name")
    fun getMembersFlowByCollectionId(collectionId: Long): Flow<List<Member>>

    @Query("DELETE FROM collection_members WHERE collectionId = :collectionId AND memberId = :memberId")
    suspend fun removeMemberFromCollection(collectionId: Long, memberId: Long)

    @Query("DELETE FROM collection_members WHERE memberId = :memberId")
    suspend fun removeAllCollectionMemberships(memberId: Long)

    @Query("SELECT COUNT(*) FROM collection_members WHERE memberId = :memberId AND collectionId = :collectionId")
    suspend fun checkMemberInCollection(memberId: Long, collectionId: Long): Int

    @Query("DELETE FROM members WHERE id = :memberId")
    suspend fun deleteMemberById(memberId: Long)
}
