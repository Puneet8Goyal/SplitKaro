package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.viewmodel.EditExpenseViewModel
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    expenseId: Long,
    viewModel: EditExpenseViewModel = hiltViewModel(),
    collectionViewModel: ExpenseCollectionViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    onDelete: () -> Unit
) {
    val description = viewModel.description
    val amount = viewModel.amount
    val paidByMemberId = viewModel.paidByMemberId
    val splitAmongMemberIds = viewModel.splitAmongMemberIds
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading
    val expense = viewModel.expense

    // Get collection members
    val collectionMembers by collectionViewModel.collectionMembers.collectAsState()
    val collectionId = expense?.collectionId
    val membersInThisCollection = if (collectionId != null) {
        collectionMembers[collectionId] ?: emptyList()
    } else emptyList()

    var paidByExpanded by remember { mutableStateOf(false) }
    var splitAmongExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load expense and members when screen starts
    LaunchedEffect(expenseId) {
        viewModel.loadExpense(expenseId)
    }

    LaunchedEffect(collectionId) {
        if (collectionId != null) {
            collectionViewModel.loadMembersForCollection(collectionId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Expense",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (expense == null && !isLoading) {
            Text(
                text = "Expense not found!",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
            return
        }

        if (membersInThisCollection.isEmpty() && !isLoading) {
            Text(
                text = "Loading members...",
                color = Color.Gray,
                modifier = Modifier.padding(8.dp)
            )
        }

        TextField(
            value = description,
            onValueChange = { viewModel.updateDescription(it) },
            label = { Text("Description") },
            placeholder = { Text("What was this expense for?") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = !isLoading
        )

        TextField(
            value = amount,
            onValueChange = { viewModel.updateAmount(it) },
            label = { Text("Amount (₹)") },
            placeholder = { Text("0.00") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            enabled = !isLoading
        )

        // Paid By Dropdown
        if (membersInThisCollection.isNotEmpty()) {
            ExposedDropdownMenuBox(
                expanded = paidByExpanded,
                onExpandedChange = { paidByExpanded = !paidByExpanded }
            ) {
                TextField(
                    value = membersInThisCollection.find { it.id == paidByMemberId }?.name
                        ?: "Select who paid",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Paid By") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paidByExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    enabled = !isLoading
                )

                ExposedDropdownMenu(
                    expanded = paidByExpanded,
                    onDismissRequest = { paidByExpanded = false }
                ) {
                    membersInThisCollection.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member.name) },
                            onClick = {
                                viewModel.updatePaidByMemberId(member.id)
                                paidByExpanded = false
                            }
                        )
                    }
                }
            }

            // Split Among Dropdown
            ExposedDropdownMenuBox(
                expanded = splitAmongExpanded,
                onExpandedChange = { splitAmongExpanded = !splitAmongExpanded }
            ) {
                TextField(
                    value = if (splitAmongMemberIds.isEmpty()) "Select who to split among"
                    else splitAmongMemberIds.mapNotNull { id ->
                        membersInThisCollection.find { it.id == id }?.name
                    }.joinToString(", "),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Split Among (${splitAmongMemberIds.size} selected)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = splitAmongExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    enabled = !isLoading
                )

                ExposedDropdownMenu(
                    expanded = splitAmongExpanded,
                    onDismissRequest = { splitAmongExpanded = false }
                ) {
                    membersInThisCollection.forEach { member ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(member.name, modifier = Modifier.weight(1f))
                                    if (member.id in splitAmongMemberIds) {
                                        Text(" ✓", color = Color.Green)
                                    }
                                }
                            },
                            onClick = {
                                val newList = splitAmongMemberIds.toMutableList()
                                if (member.id in newList) {
                                    newList.remove(member.id)
                                } else {
                                    newList.add(member.id)
                                }
                                viewModel.updateSplitAmongMemberIds(newList)
                            }
                        )
                    }

                    if (splitAmongMemberIds.isNotEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "✓ Done (${splitAmongMemberIds.size} selected)",
                                    color = Color.Green
                                )
                            },
                            onClick = {
                                splitAmongExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Show split preview
        if (splitAmongMemberIds.isNotEmpty() && amount.toDoubleOrNull() != null) {
            val amountValue = amount.toDouble()
            val perPerson = amountValue / splitAmongMemberIds.size
            Text(
                text = "Per person: ₹${String.format("%.2f", perPerson)}",
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                enabled = !isLoading
            ) {
                Text("Delete", color = Color.White)
            }

            Button(
                onClick = { viewModel.updateExpense(onSuccess) },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                enabled = !isLoading && membersInThisCollection.isNotEmpty()
            ) {
                Text(if (isLoading) "Updating..." else "Update")
            }
        }

        if (snackbarMessage.isNotEmpty()) {
            Text(
                text = snackbarMessage,
                modifier = Modifier.padding(top = 8.dp),
                color = if (snackbarMessage.contains("success", ignoreCase = true))
                    Color(0xFF4CAF50) else Color.Red
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Expense") },
            text = { Text("Are you sure you want to delete this expense? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(onDelete)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}