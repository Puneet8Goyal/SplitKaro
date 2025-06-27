package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.viewmodel.ManageMembersViewModel

@Composable
fun ManageMembersScreen(
    collectionId: Long,
    viewModel: ManageMembersViewModel = hiltViewModel(),
    onBackClick: () -> Unit = { }
) {
    val collectionMembers by viewModel.collectionMembers.collectAsState()
    val allMembers by viewModel.allMembers.collectAsState()
    val collection = viewModel.collection
    val newMemberName = viewModel.newMemberName
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading
    val showAddMemberDialog = viewModel.showAddMemberDialog
    val showRemoveDialog = viewModel.showRemoveDialog
    val memberToRemove = viewModel.memberToRemove

    val availableMembers = viewModel.getAvailableMembers()

    LaunchedEffect(collectionId) {
        viewModel.loadData(collectionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Manage Members",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        collection?.let {
            Text(
                text = "Collection: ${it.name}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp, start = 56.dp)
            )
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            else -> {
                // Current Members Section
                Text(
                    text = "Members in Collection (${collectionMembers.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (collectionMembers.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "No members yet. Add some members to start splitting expenses!",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(collectionMembers, key = { it.id }) { member ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )

                                    Text(
                                        text = member.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (collectionMembers.size > 1) {
                                        IconButton(
                                            onClick = { viewModel.openRemoveDialog(member) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove Member",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Available Members Section (if any)
                if (availableMembers.isNotEmpty()) {
                    Text(
                        text = "Add Existing Members",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(availableMembers, key = { it.id }) { member ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3E1E)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.addExistingMember(member.id, collectionId)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Color.Green,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )

                                    Text(
                                        text = member.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Floating Action Button
                FloatingActionButton(
                    onClick = { viewModel.openAddMemberDialog() },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Add New Member")
                }

                // IMPLEMENTED: Snackbar Message with tap to dismiss
                if (snackbarMessage.isNotEmpty()) {
                    Text(
                        text = snackbarMessage,
                        color = if (snackbarMessage.contains("success", ignoreCase = true) ||
                            snackbarMessage.contains("added", ignoreCase = true) ||
                            snackbarMessage.contains("removed", ignoreCase = true)
                        ) Color(0xFF4CAF50) else Color.Red,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { viewModel.clearMessage() } // IMPLEMENTED: Tap to dismiss
                    )
                }
            }
        }

        // Add Member Dialog
        if (showAddMemberDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeAddMemberDialog() },
                title = { Text("Add New Member") },
                text = {
                    Column {
                        TextField(
                            value = newMemberName,
                            onValueChange = { viewModel.updateNewMemberName(it) },
                            label = { Text("Member Name") },
                            placeholder = { Text("Enter member name") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.addNewMember(collectionId) },
                        enabled = newMemberName.trim().isNotEmpty()
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.closeAddMemberDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Remove Member Dialog
        if (showRemoveDialog && memberToRemove != null) {
            AlertDialog(
                onDismissRequest = { viewModel.closeRemoveDialog() },
                title = { Text("Remove Member") },
                text = {
                    Text("Are you sure you want to remove '${memberToRemove!!.name}' from this collection? They will no longer be able to participate in expenses for this collection.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.removeMemberFromCollection(memberToRemove!!, collectionId)
                            viewModel.closeRemoveDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Remove", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(onClick = { viewModel.closeRemoveDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
