package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.puneet8goyal.splitkaro.viewmodel.MainViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun ExpenseScreen(viewModel: MainViewModel, snackBarState: String) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.snackbarMessage) {
        if (viewModel.snackbarMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(viewModel.snackbarMessage)
            viewModel.clearSnackbar()
        }
    }

    if (showEditDialog && viewModel.selectedExpense != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Expense") },
            text = {
                Column {
                    OutlinedTextField(
                        value = viewModel.title,
                        onValueChange = { viewModel.title = it },
                        label = { Text("Title") }
                    )
                    OutlinedTextField(
                        value = viewModel.amount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                viewModel.amount = newValue
                            }
                        },
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = viewModel.paidBy,
                        onValueChange = { viewModel.paidBy = it },
                        label = { Text("Paid By") }
                    )
                    OutlinedTextField(
                        value = viewModel.groupId,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                                viewModel.groupId = newValue
                            }
                        },
                        label = { Text("Group ID") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

            },
            confirmButton = {
                TextButton(
                    onClick = {
                        runBlocking { viewModel.editExpense(viewModel.selectedExpense!!) }
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }


    Column(modifier = Modifier.padding(16.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Title:", modifier = Modifier.weight(1f), color = Color.Gray)
                    OutlinedTextField(
                        value = viewModel.title,
                        onValueChange = { viewModel.title = it },
                        modifier = Modifier
                            .weight(2f)
                            .padding(start = 8.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Amount:", modifier = Modifier.weight(1f), color = Color.Gray)
                    OutlinedTextField(
                        value = viewModel.amount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                viewModel.amount = newValue
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(2f)
                            .padding(start = 8.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Paid By:", modifier = Modifier.weight(1f), color = Color.Gray)
                    OutlinedTextField(
                        value = viewModel.paidBy,
                        onValueChange = { viewModel.paidBy = it },
                        modifier = Modifier
                            .weight(2f)
                            .padding(start = 8.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Group ID:", modifier = Modifier.weight(1f), color = Color.Gray)
                    OutlinedTextField(
                        value = viewModel.groupId,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                                viewModel.groupId = newValue
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(2f)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
        Button(
            onClick = {
                runBlocking {
                    viewModel.addExpense()
                    viewModel.clearInputs()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Add Expense and Fetch", color = Color.White)
        }
        viewModel.expenses.forEach { expense ->
            Row(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "${expense.title} : ${expense.amount}(Paid by : ${expense.paidBy})",
                    modifier = Modifier.padding(8.dp),
                    color = Color.Black
                )
                Button(
                    onClick = { viewModel.selectExpenseForEdit(expense);showEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier.padding(start = 8.dp)
                ) { Text("Edit", color = Color.White) }
                Button(
                    onClick = { runBlocking { viewModel.deleteExpense(expense) } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(8.dp))
    }
}
