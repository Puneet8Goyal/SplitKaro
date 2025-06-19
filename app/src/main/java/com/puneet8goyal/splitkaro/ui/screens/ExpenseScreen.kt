package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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

    LaunchedEffect(viewModel.snackbarMessage) {
        if (viewModel.snackbarMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(viewModel.snackbarMessage)
            viewModel.clearSnackbar()
        }
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
                        modifier = Modifier.padding(8.dp)
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
                        modifier = Modifier.weight(2f).padding(8.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Title:", modifier = Modifier.weight(1f), color = Color.Gray)
                    OutlinedTextField(
                        value = viewModel.title,
                        onValueChange = { viewModel.title = it },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = viewModel.paidBy,
            onValueChange = { viewModel.paidBy = it },
            label = { Text("Paid By") },
            modifier = Modifier.padding(8.dp)
        )
        OutlinedTextField(
            value = viewModel.groupId,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d+$"))) {
                    viewModel.groupId = newValue
                }
            },
            label = { Text("Group ID") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = {
                runBlocking {
                    viewModel.addExpense()
                    viewModel.clearInputs()
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Add Expense and fetch")
        }
        viewModel.expenses.forEach { expense ->
            Text(
                text = "${expense.title} : ${expense.amount}(Paid by : ${expense.paidBy})",
                modifier = Modifier.padding(8.dp)
            )
        }
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(8.dp))
    }
}