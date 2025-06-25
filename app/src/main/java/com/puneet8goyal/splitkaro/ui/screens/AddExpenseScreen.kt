package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.puneet8goyal.splitkaro.viewmodel.AddExpenseViewModel
import kotlinx.coroutines.delay

@Composable
fun AddExpenseScreen(viewModel: AddExpenseViewModel, onSuccess: () -> Unit) {
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading

    // Auto-clear success messages after 3 seconds
    LaunchedEffect(snackbarMessage) {
        if (snackbarMessage == "Expense added successfully!") {
            delay(3000)
            viewModel.clearMessage()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            label = { Text("Description", color = Color(0xFF03DAC6)) },
            placeholder = { Text("e.g., Dinner at restaurant") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = viewModel.amount,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                    viewModel.amount = it
                }
            },
            label = { Text("Amount (₹)", color = Color(0xFF03DAC6)) },
            placeholder = { Text("0.00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = viewModel.paidBy,
            onValueChange = { viewModel.paidBy = it },
            label = { Text("Paid By", color = Color(0xFF03DAC6)) },
            placeholder = { Text("Your name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        OutlinedTextField(
            value = viewModel.splitAmong,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                    viewModel.splitAmong = it
                }
            },
            label = { Text("Split Among (number of people)", color = Color(0xFF03DAC6)) },
            placeholder = { Text("e.g., 4") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        // Show calculated per-person amount if valid input
        if (viewModel.amount.isNotEmpty() && viewModel.splitAmong.isNotEmpty()) {
            val amount = viewModel.amount.toDoubleOrNull()
            val splitCount = viewModel.splitAmong.toIntOrNull()
            if (amount != null && splitCount != null && splitCount > 0) {
                val perPerson = amount / splitCount
                Text(
                    text = "Per person: ₹${"%.2f".format(perPerson)}",
                    color = Color(0xFF03DAC6),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Button(
            onClick = { viewModel.addExpense(onSuccess) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC6)),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = if (isLoading) "Adding..." else "Add Expense",
                color = Color.Black
            )
        }

        if (snackbarMessage.isNotEmpty()) {
            val messageColor = if (snackbarMessage == "Expense added successfully!") {
                Color(0xFF03DAC6)
            } else {
                Color.Red
            }

            Text(
                text = snackbarMessage,
                color = messageColor,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}