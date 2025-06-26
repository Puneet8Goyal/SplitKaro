package com.puneet8goyal.splitkaro.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneet8goyal.splitkaro.data.Group
import com.puneet8goyal.splitkaro.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    var snackbarMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    init {
        loadGroups()
    }

    private fun loadGroups() {
        isLoading = true
        viewModelScope.launch {
            try {
                _groups.value = groupRepository.getAllGroups()
            } catch (e: Exception) {
                snackbarMessage = "Error loading groups: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun createGroup(name: String) {
        if (isLoading) return

        if (name.trim().isEmpty()) {
            snackbarMessage = "Group name is required"
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val group = Group(name = name.trim())
                groupRepository.insertGroup(group)
                snackbarMessage = "Group created successfully!"
                loadGroups() // Refresh group list
            } catch (e: Exception) {
                snackbarMessage = "Error creating group: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}