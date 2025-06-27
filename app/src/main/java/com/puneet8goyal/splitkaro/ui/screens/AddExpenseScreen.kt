package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.puneet8goyal.splitkaro.viewmodel.AddExpenseViewModel
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: AddExpenseViewModel = hiltViewModel(),
    collectionViewModel: ExpenseCollectionViewModel = hiltViewModel(),
    collectionId: Long,
    onSuccess: () -> Unit
) {
    val description = viewModel.description
    val amount = viewModel.amount
    val paidByMemberId = viewModel.paidByMemberId
    val splitAmongMemberIds = viewModel.splitAmongMemberIds
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading

    // Get members for this specific collection
    val collectionMembers by collectionViewModel.collectionMembers.collectAsState()
    val membersInThisCollection = collectionMembers[collectionId] ?: emptyList()

    var paidByExpanded by remember { mutableStateOf(false) }
    var splitAmongExpanded by remember { mutableStateOf(false) }

    // Load collection members when screen starts
    LaunchedEffect(collectionId) {
        println("DEBUG AddExpenseScreen: Loading members for collection $collectionId")
        collectionViewModel.loadMembersForCollection(collectionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Add Expense",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Show member count for this collection (DEBUG INFO)
        Text(
            text = "Members in this collection: ${membersInThisCollection.size}",
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (membersInThisCollection.isNotEmpty()) {
            Text(
                text = "Members: ${membersInThisCollection.joinToString(", ") { it.name }}",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (membersInThisCollection.isEmpty()) {
            Text(
                text = "No members in this collection! Please add members first.",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            return
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

        // Paid By Dropdown - PROPER IMPLEMENTATION
        ExposedDropdownMenuBox(
            expanded = paidByExpanded,
            onExpandedChange = {
                paidByExpanded = !paidByExpanded
                println("DEBUG: Paid By dropdown expanded: $paidByExpanded")
            }
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
                println("DEBUG: Rendering Paid By dropdown with ${membersInThisCollection.size} members")
                membersInThisCollection.forEach { member ->
                    DropdownMenuItem(
                        text = { Text(member.name) },
                        onClick = {
                            println("DEBUG: Selected Paid By member: ${member.name} (ID: ${member.id})")
                            viewModel.updatePaidByMemberId(member.id)
                            paidByExpanded = false
                        }
                    )
                }
            }
        }

        // Split Among Dropdown - PROPER IMPLEMENTATION
        ExposedDropdownMenuBox(
            expanded = splitAmongExpanded,
            onExpandedChange = {
                splitAmongExpanded = !splitAmongExpanded
                println("DEBUG: Split Among dropdown expanded: $splitAmongExpanded")
            }
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
                println("DEBUG: Rendering Split Among dropdown with ${membersInThisCollection.size} members")
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
                                println("DEBUG: Removed ${member.name} from split")
                            } else {
                                newList.add(member.id)
                                println("DEBUG: Added ${member.name} to split")
                            }
                            viewModel.updateSplitAmongMemberIds(newList)
                            // Don't close dropdown for multi-select
                        }
                    )
                }

                // Add "Done" button for multi-select
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

        Button(
            onClick = { viewModel.addExpense(collectionId = collectionId, onSuccess = onSuccess) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && membersInThisCollection.isNotEmpty()
        ) {
            Text(if (isLoading) "Adding..." else "Add Expense")
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
}