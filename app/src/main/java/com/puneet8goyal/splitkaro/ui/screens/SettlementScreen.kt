package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.utils.AppUtils
import com.puneet8goyal.splitkaro.utils.MemberAvatar
import com.puneet8goyal.splitkaro.viewmodel.SettlementViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementScreen(
    collectionId: Long,
    viewModel: SettlementViewModel = hiltViewModel(),
    onBackClick: () -> Unit = { }
) {
    val expenses = viewModel.expenses
    val members = viewModel.members
    val memberBalances = viewModel.memberBalances
    val unsettledSettlements =
        viewModel.getUnsettledSettlements()  // FIXED: Get unsettled settlements
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val settledCount = viewModel.getSettledCount()  // RESTORED
    val allSettled = viewModel.areAllSettled()      // RESTORED

    var isRefreshing by remember { mutableStateOf(false) }

    // Handle refresh state
    LaunchedEffect(isLoading) {
        if (isLoading) {
            isRefreshing = true
        } else {
            delay(300)
            isRefreshing = false
        }
    }

    LaunchedEffect(collectionId) {
        viewModel.loadSettlementData(collectionId)
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refreshData(collectionId)
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
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
                    text = "Settlement",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }

            when {
                isRefreshing -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Refreshing settlement data...",
                                color = Color.White,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }

                errorMessage.isNotEmpty() -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4E1E1E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { viewModel.clearErrorMessage() }
                    ) {
                        Text(
                            text = "⚠️ $errorMessage\n(Tap to dismiss)",
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                expenses.isEmpty() -> {
                    Text(
                        text = "No expenses to settle!",
                        color = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // Summary Card
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "📊 Summary",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = "Total Amount: ${AppUtils.formatCurrency(viewModel.getTotalCollectionAmount())}",
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    Text(
                                        text = "Total Expenses: ${expenses.size}",
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )

                                    Text(
                                        text = "Members: ${members.size}",
                                        color = Color.White,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )

                                    // RESTORED: Show settled count
                                    if (settledCount > 0) {
                                        Text(
                                            text = "Settled Payments: $settledCount",
                                            color = Color(0xFF4CAF50),
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Member Balances Section
                        item {
                            Text(
                                text = "Member Balances",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(memberBalances) { balance ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        balance.netBalance > 0 -> Color(0xFF1E4E1E)
                                        balance.netBalance < 0 -> Color(0xFF4E1E1E)
                                        else -> Color(0xFF2E2E2E)
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MemberAvatar(member = balance.member, size = 40)

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 12.dp)
                                    ) {
                                        Text(
                                            text = balance.member.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Paid: ${AppUtils.formatCurrency(balance.totalPaid)}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall
                                            )

                                            Text(
                                                text = "Owes: ${AppUtils.formatCurrency(balance.totalOwed)}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Text(
                                            text = when {
                                                balance.netBalance > 0 -> "Gets back: ${
                                                    AppUtils.formatCurrency(balance.netBalance)
                                                }"

                                                balance.netBalance < 0 -> "Owes: ${
                                                    AppUtils.formatCurrency(-balance.netBalance)
                                                }"

                                                else -> "Settled ✓"
                                            },
                                            color = when {
                                                balance.netBalance > 0 -> Color(0xFF4CAF50)
                                                balance.netBalance < 0 -> Color(0xFFF44336)
                                                else -> Color(0xFF4CAF50)
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // FIXED: Settlements Section - Now using unsettled settlements
                        if (unsettledSettlements.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Pending Settlements",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            items(unsettledSettlements) { settlementWithStatus ->
                                SettlementCard(
                                    settlementWithStatus = settlementWithStatus,
                                    onMarkAsSettled = {
                                        // FIXED: Now actually calls the ViewModel function
                                        viewModel.markSettlementAsSettled(settlementWithStatus)
                                    }
                                )
                            }
                        }

                        // RESTORED: All settled message
                        if (allSettled || (unsettledSettlements.isEmpty() && settledCount > 0)) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFF1E4E1E
                                        )
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "🎉 All settled! No payments needed.",
                                        color = Color(0xFF4CAF50),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        if (unsettledSettlements.isEmpty() && settledCount == 0 && memberBalances.all { it.netBalance == 0.0 }) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFF1E4E1E
                                        )
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "🎉 Perfect balance! Everyone has paid their share.",
                                        color = Color(0xFF4CAF50),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Center
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

// FIXED: SettlementCard now works with the proper data structure
@Composable
fun SettlementCard(
    settlementWithStatus: SettlementViewModel.SettlementWithStatus,
    onMarkAsSettled: () -> Unit
) {
    val settlement = settlementWithStatus.settlement

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3E2E5E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    MemberAvatar(member = settlement.fromMember, size = 40)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "pays",
                            tint = Color.White
                        )

                        Text(
                            text = AppUtils.formatCurrency(settlement.amount),
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    MemberAvatar(member = settlement.toMember, size = 40)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${settlement.fromMember.name} → ${settlement.toMember.name}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )

                // FIXED: Now the button actually works!
                Button(
                    onClick = onMarkAsSettled,
                    modifier = Modifier.padding(start = 8.dp),
                    enabled = settlementWithStatus.settlementRecord != null  // Only enable if we have a record
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Mark as Settled",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Mark Settled")
                }
            }
        }
    }
}
