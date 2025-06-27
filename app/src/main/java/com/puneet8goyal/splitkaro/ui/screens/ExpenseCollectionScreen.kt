package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel

@Composable
fun ExpenseCollectionScreen(
    viewModel: ExpenseCollectionViewModel = hiltViewModel(),
    onCollectionClick: (Long) -> Unit
) {
    val collections by viewModel.collections.collectAsState()
    val members by viewModel.members.collectAsState()
    val collectionMembers by viewModel.collectionMembers.collectAsState()
    val newCollectionName = viewModel.newCollectionName
    val newMemberName = viewModel.newMemberName
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading
    val showCollectionDialog = viewModel.showCollectionDialog
    val showMemberDialog = viewModel.showMemberDialog
    val currentCollectionId = viewModel.currentCollectionId

    var showDeleteDialog by remember { mutableStateOf(false) }
    var collectionToDelete by remember {
        mutableStateOf<com.puneet8goyal.splitkaro.data.ExpenseCollection?>(
            null
        )
    }
    var expandedCollectionMenu by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Expense Collections",
            modifier = Modifier.padding(bottom = 16.dp),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        when {
            isLoading -> {
                Text(
                    text = "Loading collections...",
                    modifier = Modifier.padding(8.dp),
                    color = Color.Gray
                )
            }

            collections.isEmpty() -> {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "No collections yet! Create one to start splitting expenses with friends.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(collections, key = { it.id }) { collection ->
                        val membersInCollection = collectionMembers[collection.id] ?: emptyList()

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                // Main collection info - clickable
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onCollectionClick(collection.id) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = collection.name,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Members: ${membersInCollection.size}",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        if (membersInCollection.isNotEmpty()) {
                                            Text(
                                                text = membersInCollection.joinToString(", ") { it.name },
                                                color = Color.Gray,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }

                                    // Action buttons
                                    IconButton(onClick = { viewModel.openMemberDialog(collection.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Manage Members",
                                            tint = Color.White
                                        )
                                    }

                                    // More options menu
                                    IconButton(onClick = {
                                        expandedCollectionMenu =
                                            if (expandedCollectionMenu == collection.id) null else collection.id
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "More Options",
                                            tint = Color.White
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expandedCollectionMenu == collection.id,
                                        onDismissRequest = { expandedCollectionMenu = null }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Delete Collection") },
                                            onClick = {
                                                collectionToDelete = collection
                                                showDeleteDialog = true
                                                expandedCollectionMenu = null
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = null,
                                                    tint = Color.Red
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.openCollectionDialog() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Creating..." else "Create New Collection")
        }

        if (snackbarMessage.isNotEmpty()) {
            Text(
                text = snackbarMessage,
                color = if (snackbarMessage.contains("success", ignoreCase = true) ||
                    snackbarMessage.contains("created", ignoreCase = true) ||
                    snackbarMessage.contains("added", ignoreCase = true)
                )
                    Color(0xFF4CAF50) else Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Collection Creation Dialog
    if (showCollectionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeCollectionDialog() },
            title = { Text("Create New Collection") },
            text = {
                TextField(
                    value = newCollectionName,
                    onValueChange = { viewModel.updateNewCollectionName(it) },
                    label = { Text("Collection Name") },
                    placeholder = { Text("e.g., 'Trip to Goa', 'Flatmates'") }
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.createCollection() },
                    enabled = newCollectionName.trim().isNotEmpty()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.closeCollectionDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Member Management Dialog
    if (showMemberDialog && currentCollectionId != null) {
        val membersInCurrentCollection = collectionMembers[currentCollectionId] ?: emptyList()
        val membersNotInCollection = members.filter { member ->
            membersInCurrentCollection.none { it.id == member.id }
        }

        AlertDialog(
            onDismissRequest = { viewModel.closeMemberDialog() },
            title = {
                Text("Manage Members")
            },
            text = {
                Column {
                    // Current members section
                    if (membersInCurrentCollection.isNotEmpty()) {
                        Text(
                            text = "Current Members:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                        membersInCurrentCollection.forEach { member ->
                            Text(
                                text = "✓ ${member.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    // Add new member section
                    Text(
                        text = "Add New Member:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = newMemberName,
                            onValueChange = { viewModel.updateNewMemberName(it) },
                            label = { Text("Member Name") },
                            placeholder = { Text("Enter name") },
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { viewModel.createAndAddNewMember() },
                            enabled = newMemberName.trim().isNotEmpty(),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text("Add")
                        }
                    }

                    // Add existing members section
                    if (membersNotInCollection.isNotEmpty()) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Text(
                            text = "Add Existing Members:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        LazyColumn {
                            items(membersNotInCollection, key = { it.id }) { member ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.addExistingMemberToCollection(member.id)
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "+ ${member.name}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.closeMemberDialog() }) {
                    Text("Done")
                }
            }
        )
    }

    // Delete Collection Dialog
    if (showDeleteDialog && collectionToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Collection") },
            text = {
                Text("Are you sure you want to delete '${collectionToDelete!!.name}'? This will also delete all expenses in this collection. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // TODO: Implement delete collection functionality
                        viewModel.deleteCollection(collectionToDelete!!)
                        showDeleteDialog = false
                        collectionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDeleteDialog = false
                    collectionToDelete = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}