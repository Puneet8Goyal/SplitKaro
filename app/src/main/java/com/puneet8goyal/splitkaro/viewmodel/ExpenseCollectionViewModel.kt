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
import com.puneet8goyal.splitkaro.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseCollectionViewModel @Inject constructor(
    private val collectionRepository: ExpenseCollectionRepository,
    private val memberRepository: MemberRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _collections = MutableStateFlow<List<ExpenseCollection>>(emptyList())
    val collections: StateFlow<List<ExpenseCollection>> = _collections.asStateFlow()

    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members.asStateFlow()

    private val _collectionMembers = MutableStateFlow<Map<Long, List<Member>>>(emptyMap())
    val collectionMembers: StateFlow<Map<Long, List<Member>>> = _collectionMembers.asStateFlow()

    var newCollectionName by mutableStateOf("")
    var newMemberName by mutableStateOf("")
    var snackbarMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var isRefreshing by mutableStateOf(false) // FIXED: Add refresh state
    var showCollectionDialog by mutableStateOf(false)
    var showMemberDialog by mutableStateOf(false)
    var currentCollectionId by mutableStateOf<Long?>(null)

    init {
        loadCollections()
        loadMembers()
    }

    fun loadCollections() {
        isLoading = true
        viewModelScope.launch {
            try {
                val collectionsList = collectionRepository.getAllCollections()
                _collections.value = collectionsList
                println("DEBUG: Loaded ${collectionsList.size} collections")
                loadMembersForAllCollections()
            } catch (e: Exception) {
                snackbarMessage = "Error loading collections: ${e.message}"
                println("DEBUG: Error loading collections: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    // FIXED: Add proper refresh method
    fun refreshCollections() {
        isRefreshing = true
        viewModelScope.launch {
            try {
                val collectionsList = collectionRepository.getAllCollections()
                _collections.value = collectionsList
                println("DEBUG: Refreshed ${collectionsList.size} collections")
                loadMembersForAllCollections()
            } catch (e: Exception) {
                snackbarMessage = "Error refreshing collections: ${e.message}"
                println("DEBUG: Error refreshing collections: ${e.message}")
            } finally {
                isRefreshing = false
            }
        }
    }

    private fun loadMembers() {
        viewModelScope.launch {
            try {
                val membersList = memberRepository.getAllMembers()
                _members.value = membersList
                println("DEBUG: Loaded ${membersList.size} total members")
            } catch (e: Exception) {
                println("DEBUG: Error loading members: ${e.message}")
            }
        }
    }

    private fun loadMembersForAllCollections() {
        viewModelScope.launch {
            try {
                val membersByCollectionMap = mutableMapOf<Long, List<Member>>()
                _collections.value.forEach { collection ->
                    val membersForCollection =
                        memberRepository.getMembersByCollectionId(collection.id)
                    membersByCollectionMap[collection.id] = membersForCollection
                    println("DEBUG: Collection ${collection.id} (${collection.name}) has ${membersForCollection.size} members")
                }

                _collectionMembers.value = membersByCollectionMap
            } catch (e: Exception) {
                println("DEBUG: Error loading members for collections: ${e.message}")
            }
        }
    }

    fun getMembersByCollectionId(collectionId: Long): List<Member> {
        return _collectionMembers.value[collectionId] ?: emptyList()
    }

    // FIXED: Validate duplicate group names and automatically add current user
    fun createCollection() {
        if (isLoading || newCollectionName.trim().isEmpty()) {
            snackbarMessage = "Collection name is required"
            return
        }

        // NEW: Check for duplicate group names
        val trimmedName = newCollectionName.trim()
        val existingCollection = _collections.value.find {
            it.name.equals(trimmedName, ignoreCase = true)
        }

        if (existingCollection != null) {
            snackbarMessage =
                "A group with this name already exists. Please choose a different name."
            return
        }

        val currentUserMemberId = userPreferences.getCurrentUserMemberId()
        if (currentUserMemberId == -1L) {
            snackbarMessage = "User not properly set up. Please restart the app."
            return
        }

        isLoading = true
        snackbarMessage = ""

        viewModelScope.launch {
            try {
                val collection = ExpenseCollection(name = trimmedName)
                println("DEBUG: Creating collection: ${collection.name}")

                collectionRepository.insertCollection(collection).fold(
                    onSuccess = { generatedId ->
                        println("DEBUG: Collection created successfully with ID: $generatedId")

                        // CRITICAL: Automatically add current user to the group
                        val currentUserCollectionMember = CollectionMember(
                            collectionId = generatedId,
                            memberId = currentUserMemberId
                        )

                        viewModelScope.launch {
                            memberRepository.insertCollectionMember(currentUserCollectionMember)
                                .fold(
                                    onSuccess = {
                                        println("DEBUG: Current user automatically added to collection $generatedId")
                                        snackbarMessage =
                                            "Group '${trimmedName}' created! You have been added as a member."
                                        newCollectionName = ""
                                        closeCollectionDialog()
                                        refreshCollections() // FIXED: Use refresh method

                                        // Open member dialog to add other members
                                        currentCollectionId = generatedId
                                        showMemberDialog = true
                                    },
                                    onFailure = { exception ->
                                        println("DEBUG: Failed to add current user to collection: ${exception.message}")
                                        snackbarMessage =
                                            "Group created but failed to add you as member: ${exception.message}"
                                        refreshCollections()
                                    }
                                )
                        }
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to create collection: ${exception.message}")
                        snackbarMessage = "Error creating group: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Exception creating collection: ${e.message}")
                snackbarMessage = "Error creating group: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addExistingMemberToCollection(memberId: Long) {
        val collectionId = currentCollectionId ?: return

        viewModelScope.launch {
            try {
                val collectionMember =
                    CollectionMember(collectionId = collectionId, memberId = memberId)

                memberRepository.insertCollectionMember(collectionMember).fold(
                    onSuccess = {
                        println("DEBUG: Existing member $memberId added to collection $collectionId")
                        snackbarMessage = "Member added to group!"
                        loadMembersForAllCollections()
                    },
                    onFailure = { exception ->
                        println("DEBUG: Error adding existing member: ${exception.message}")
                        snackbarMessage = "Error adding member: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Exception adding existing member: ${e.message}")
                snackbarMessage = "Error adding member: ${e.message}"
            }
        }
    }

    // FIXED: Validate duplicate member names in the same group
    fun createAndAddNewMember() {
        if (newMemberName.trim().isEmpty()) {
            snackbarMessage = "Member name is required"
            return
        }

        val collectionId = currentCollectionId ?: return
        val trimmedMemberName = newMemberName.trim()

        // NEW: Check for duplicate member names in this specific group
        val existingMembersInGroup = _collectionMembers.value[collectionId] ?: emptyList()
        val duplicateMember = existingMembersInGroup.find {
            it.name.equals(trimmedMemberName, ignoreCase = true)
        }

        if (duplicateMember != null) {
            snackbarMessage =
                "A member with this name already exists in this group. Please choose a different name."
            return
        }

        viewModelScope.launch {
            try {
                val member = Member(name = trimmedMemberName)
                println("DEBUG: Creating new member: ${member.name}")

                memberRepository.insertMember(member).fold(
                    onSuccess = { generatedMemberId ->
                        println("DEBUG: Member created with ID: $generatedMemberId")
                        newMemberName = ""
                        loadMembers()

                        // Add the new member to current collection
                        val collectionMember = CollectionMember(
                            collectionId = collectionId,
                            memberId = generatedMemberId
                        )

                        viewModelScope.launch {
                            memberRepository.insertCollectionMember(collectionMember).fold(
                                onSuccess = {
                                    println("DEBUG: New member $generatedMemberId added to collection $collectionId")
                                    snackbarMessage =
                                        "New member '${trimmedMemberName}' created and added!"
                                    loadMembersForAllCollections()
                                },
                                onFailure = { exception ->
                                    println("DEBUG: Error adding new member to collection: ${exception.message}")
                                    snackbarMessage =
                                        "Member created but error adding to group: ${exception.message}"
                                }
                            )
                        }
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to create member: ${exception.message}")
                        snackbarMessage = "Error creating member: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Exception creating member: ${e.message}")
                snackbarMessage = "Error creating member: ${e.message}"
            }
        }
    }

    fun updateNewCollectionName(name: String) {
        newCollectionName = name
        if (snackbarMessage.isNotEmpty()) snackbarMessage = ""
    }

    fun updateNewMemberName(name: String) {
        newMemberName = name
        if (snackbarMessage.isNotEmpty()) snackbarMessage = ""
    }

    fun clearMessage() {
        snackbarMessage = ""
    }

    fun openCollectionDialog() {
        showCollectionDialog = true
    }

    fun closeCollectionDialog() {
        showCollectionDialog = false
        newCollectionName = ""
    }

    fun openMemberDialog(collectionId: Long) {
        currentCollectionId = collectionId
        showMemberDialog = true
    }

    fun loadMembersForCollection(collectionId: Long) {
        viewModelScope.launch {
            try {
                println("DEBUG: Loading members specifically for collection $collectionId")
                val membersForCollection = memberRepository.getMembersByCollectionId(collectionId)
                println("DEBUG: Found ${membersForCollection.size} members for collection $collectionId")

                val currentMap = _collectionMembers.value.toMutableMap()
                currentMap[collectionId] = membersForCollection
                _collectionMembers.value = currentMap

                println("DEBUG: Updated collectionMembers map, collection $collectionId now has ${membersForCollection.size} members")
            } catch (e: Exception) {
                println("DEBUG: Error loading members for collection $collectionId: ${e.message}")
            }
        }
    }

    fun deleteCollection(collection: com.puneet8goyal.splitkaro.data.ExpenseCollection) {
        if (isLoading) return

        isLoading = true
        snackbarMessage = ""

        viewModelScope.launch {
            try {
                collectionRepository.deleteCollection(collection).fold(
                    onSuccess = {
                        println("DEBUG: Collection deleted successfully")
                        snackbarMessage = "Group deleted successfully!"
                        refreshCollections() // FIXED: Use refresh method
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to delete collection: ${exception.message}")
                        snackbarMessage = "Error deleting group: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Exception deleting collection: ${e.message}")
                snackbarMessage = "Error deleting group: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun closeMemberDialog() {
        showMemberDialog = false
        newMemberName = ""
        currentCollectionId = null
    }
}