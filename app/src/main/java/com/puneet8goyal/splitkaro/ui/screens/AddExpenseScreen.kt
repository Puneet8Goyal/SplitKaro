package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.viewmodel.AddExpenseViewModel

@Composable
fun AddExpenseScreen(
    viewModel: AddExpenseViewModel = hiltViewModel(),
    groupId: Long,
    onSuccess: () -> Unit
) {
    val description = viewModel.description
    val amount = viewModel.amount
    val paidBy = viewModel.paidBy
    val splitAmong = viewModel.splitAmong
    val splitAmongPeople = viewModel.splitAmongPeople
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = description,
            onValueChange = { viewModel.description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = amount,
            onValueChange = { viewModel.amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = paidBy,
            onValueChange = { viewModel.paidBy = it },
            label = { Text("Paid By") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = splitAmong,
            onValueChange = { viewModel.splitAmong = it },
            label = { Text("Split Among (Number)") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = splitAmongPeople,
            onValueChange = { viewModel.splitAmongPeople = it },
            label = { Text("Split Among (Names)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.addExpense(groupId = groupId, onSuccess = onSuccess) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !isLoading
        ) {
            Text("Add Expense")

            if (snackbarMessage.isNotEmpty()) {
                Text(
                    text = snackbarMessage,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}