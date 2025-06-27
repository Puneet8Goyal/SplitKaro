package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.CollectionMember
import com.puneet8goyal.splitkaro.data.ExpenseCollection
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.repository.ExpenseCollectionRepository
import com.puneet8goyal.splitkaro.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageMembersViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    private val collectionRepository: ExpenseCollectionRepository
) : ViewModel() {

    private val _collectionMembers = MutableStateFlow<List<Member>>(emptyList())
    val collectionMembers: StateFlow<List<Member>> = _collectionMembers.asStateFlow()

    private val _allMembers = MutableStateFlow<List<Member>>(emptyList())
    val allMembers: StateFlow<List<Member>> = _allMembers.asStateFlow()

    var collection by mutableStateOf<ExpenseCollection?>(null)
    var newMemberName by mutableStateOf("")
    var snackbarMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var showAddMemberDialog by mutableStateOf(false)
    var showRemoveDialog by mutableStateOf(false)
    var memberToRemove by mutableStateOf<Member?>(null)

    fun loadData(collectionId: Long) {
        isLoading = true
        viewModelScope.launch {
            try {
                // Load collection info
                collection = collectionRepository.getCollectionById(collectionId)

                // Load members in this collection
                val membersInCollection = memberRepository.getMembersByCollectionId(collectionId)
                _collectionMembers.value = membersInCollection

                // Load all members
                val allMembersList = memberRepository.getAllMembers()
                _allMembers.value = allMembersList

                println("DEBUG ManageMembers: Collection has ${membersInCollection.size} members")

            } catch (e: Exception) {
                snackbarMessage = "Error loading data: ${e.message}"
                println("DEBUG ManageMembers error: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun addNewMember(collectionId: Long) {
        if (newMemberName.trim().isEmpty()) {
            snackbarMessage = "Member name is required"
            return
        }

        isLoading = true
        snackbarMessage = ""
        viewModelScope.launch {
            try {
                val member = Member(name = newMemberName.trim())
                memberRepository.insertMember(member).fold(
                    onSuccess = { generatedMemberId ->
                        // Add to collection
                        val collectionMember = CollectionMember(
                            collectionId = collectionId,
                            memberId = generatedMemberId
                        )
                        memberRepository.insertCollectionMember(collectionMember).fold(
                            onSuccess = {
                                newMemberName = ""
                                showAddMemberDialog = false
                                snackbarMessage = "Member added successfully!"
                                loadData(collectionId) // Refresh data
                            },
                            onFailure = { exception ->
                                snackbarMessage =
                                    "Error adding member to collection: ${exception.message}"
                            }
                        )
                    },
                    onFailure = { exception ->
                        snackbarMessage = "Error creating member: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                snackbarMessage = "Error adding member: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addExistingMember(memberId: Long, collectionId: Long) {
        viewModelScope.launch {
            try {
                val collectionMember = CollectionMember(
                    collectionId = collectionId,
                    memberId = memberId
                )
                memberRepository.insertCollectionMember(collectionMember).fold(
                    onSuccess = {
                        snackbarMessage = "Member added to collection!"
                        loadData(collectionId) // Refresh data
                    },
                    onFailure = { exception ->
                        snackbarMessage = "Error adding member: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                snackbarMessage = "Error adding member: ${e.message}"
            }
        }
    }

    fun removeMemberFromCollection(member: Member, collectionId: Long) {
        viewModelScope.launch {
            try {
                memberRepository.removeMemberFromCollection(collectionId, member.id).fold(
                    onSuccess = {
                        snackbarMessage = "${member.name} removed from collection"
                        loadData(collectionId) // Refresh data
                    },
                    onFailure = { exception ->
                        snackbarMessage = "Error removing member: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                snackbarMessage = "Error removing member: ${e.message}"
            }
        }
    }

    fun updateNewMemberName(name: String) {
        newMemberName = name
        if (snackbarMessage.isNotEmpty()) snackbarMessage = ""
    }

    fun openAddMemberDialog() {
        showAddMemberDialog = true
    }

    fun closeAddMemberDialog() {
        showAddMemberDialog = false
        newMemberName = ""
    }

    fun openRemoveDialog(member: Member) {
        memberToRemove = member
        showRemoveDialog = true
    }

    fun closeRemoveDialog() {
        showRemoveDialog = false
        memberToRemove = null
    }

    fun clearMessage() {
        snackbarMessage = ""
    }

    fun getAvailableMembers(): List<Member> {
        val currentMemberIds = _collectionMembers.value.map { it.id }.toSet()
        return _allMembers.value.filter { it.id !in currentMemberIds }
    }
}