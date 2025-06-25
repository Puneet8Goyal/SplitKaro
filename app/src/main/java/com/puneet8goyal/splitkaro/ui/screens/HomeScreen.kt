package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puneet8goyal.splitkaro.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val expenses = viewModel.expenses
    val summary = viewModel.expenseSummary

    Column(modifier = Modifier.padding(16.dp)) {
        // Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Expense Summary",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF03DAC6)
                )

                Spacer(modifier = Modifier.height(8.dp))

                SummaryRow("Total Expenses:", "${summary.totalExpenses}")
                SummaryRow("Total Amount:", "₹${"%.2f".format(summary.totalAmount)}")
                SummaryRow("Your Share:", "₹${"%.2f".format(summary.userShare)}")
                SummaryRow(
                    "You Should Receive:",
                    "₹${"%.2f".format(summary.overallBalance)}",
                    valueColor = if (summary.overallBalance >= 0) Color(0xFF4CAF50) else Color(
                        0xFFFF5252
                    )
                )

                if (summary.totalExpenses > 0) {
                    SummaryRow("Average Expense:", "₹${"%.2f".format(summary.averageExpense)}")
                }
            }
        }

        // Expenses List
        Text(
            text = "Recent Expenses",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF03DAC6),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (expenses.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No expenses added yet. Start by adding your first expense!",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(expenses.reversed()) { expense -> // Show newest first
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = expense.description,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Paid by: ${expense.paidBy}",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "₹${expense.amount}",
                                        color = Color(0xFF03DAC6),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Split ${expense.splitAmong} ways",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "₹${"%.2f".format(expense.perPersonAmount)}/person",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}