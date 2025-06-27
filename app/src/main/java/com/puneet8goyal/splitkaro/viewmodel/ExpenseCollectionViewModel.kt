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
class ExpenseCollectionViewModel @Inject constructor(
    private val collectionRepository: ExpenseCollectionRepository,
    private val memberRepository: MemberRepository
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
    var showCollectionDialog by mutableStateOf(false)
    var showMemberDialog by mutableStateOf(false)
    var currentCollectionId by mutableStateOf<Long?>(null)

    init {
        loadCollections()
        loadMembers()
    }

    private fun loadCollections() {
        isLoading = true
        viewModelScope.launch {
            try {
                val collectionsList = collectionRepository.getAllCollections()
                _collections.value = collectionsList
                println("DEBUG: Loaded ${collectionsList.size} collections")

                // Load members for each collection
                loadMembersForAllCollections()
            } catch (e: Exception) {
                snackbarMessage = "Error loading collections: ${e.message}"
                println("DEBUG: Error loading collections: ${e.message}")
            } finally {
                isLoading = false
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

    fun createCollection() {
        if (isLoading || newCollectionName.trim().isEmpty()) {
            snackbarMessage = "Collection name is required"
            return
        }

        isLoading = true
        snackbarMessage = ""
        viewModelScope.launch {
            try {
                val collection = ExpenseCollection(name = newCollectionName.trim())
                println("DEBUG: Creating collection: ${collection.name}")

                collectionRepository.insertCollection(collection).fold(
                    onSuccess = { generatedId ->
                        println("DEBUG: Collection created successfully with ID: $generatedId")
                        snackbarMessage = "Collection created! Now add members."
                        newCollectionName = ""
                        closeCollectionDialog()
                        loadCollections()

                        // Open member dialog for the newly created collection
                        currentCollectionId = generatedId
                        showMemberDialog = true
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to create collection: ${exception.message}")
                        snackbarMessage = "Error creating collection: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Exception creating collection: ${e.message}")
                snackbarMessage = "Error creating collection: ${e.message}"
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
                        snackbarMessage = "Member added to collection!"
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

    fun createAndAddNewMember() {
        if (newMemberName.trim().isEmpty()) {
            snackbarMessage = "Member name is required"
            return
        }

        val collectionId = currentCollectionId ?: return

        viewModelScope.launch {
            try {
                val member = Member(name = newMemberName.trim())
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
                                    snackbarMessage = "New member created and added!"
                                    loadMembersForAllCollections()
                                },
                                onFailure = { exception ->
                                    println("DEBUG: Error adding new member to collection: ${exception.message}")
                                    snackbarMessage =
                                        "Member created but error adding to collection: ${exception.message}"
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
                        snackbarMessage = "Collection deleted successfully!"
                        loadCollections() // Refresh the list
                    },
                    onFailure = { exception ->
                        println("DEBUG: Failed to delete collection: ${exception.message}")
                        snackbarMessage = "Error deleting collection: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Exception deleting collection: ${e.message}")
                snackbarMessage = "Error deleting collection: ${e.message}"
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