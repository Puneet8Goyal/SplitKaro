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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.utils.AppUtils
import com.puneet8goyal.splitkaro.utils.MemberAvatar
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel
import com.puneet8goyal.splitkaro.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    collectionViewModel: ExpenseCollectionViewModel = hiltViewModel(),
    collectionId: Long,
    onAddExpenseClick: () -> Unit,
    onEditExpenseClick: (Long) -> Unit,
    onSettlementClick: () -> Unit,
    onManageMembersClick: () -> Unit
) {
    val expenses = viewModel.expenses
    val expenseSummary = viewModel.expenseSummary
    val isLoading = viewModel.isLoading
    val isRefreshing = viewModel.isRefreshing
    val errorMessage = viewModel.errorMessage
    val searchQuery = viewModel.searchQuery
    val hasActiveFilters = viewModel.hasActiveFilters()

    val collectionMembers by collectionViewModel.collectionMembers.collectAsState()
    val membersInThisCollection = collectionMembers[collectionId] ?: emptyList()

    var showSearch by remember { mutableStateOf(false) }

    // Load data only once on first launch
    LaunchedEffect(collectionId) {
        viewModel.loadExpenses(collectionId)
        collectionViewModel.loadMembersForCollection(collectionId)
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refreshExpenses(collectionId)
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
            Text(
                text = "Expenses",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Search button
            OutlinedButton(
                onClick = { showSearch = !showSearch },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (showSearch) Color(0xFF4CAF50) else Color.White,
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (showSearch) Icons.Default.Clear else Icons.Default.Search,
                    contentDescription = if (showSearch) "Close Search" else "Search",
                    tint = if (showSearch) Color(0xFF4CAF50) else Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    if (showSearch) "Close Search" else "Search Expenses",
                    color = if (showSearch) Color(0xFF4CAF50) else Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Main action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onManageMembersClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3),
                        contentColor = Color.White
                    )
                ) {
                    Text("👥 Members", color = Color.White, fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = onSettlementClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text("💰 Settlement", color = Color.White, fontWeight = FontWeight.Medium)
                }
            }

            // Search bar
            if (showSearch) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchExpenses(it) },
                        label = { Text("Search expenses...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF4CAF50))
                        },
                        trailingIcon = if (searchQuery.isNotBlank()) {
                            {
                                IconButton(onClick = { viewModel.searchExpenses("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        } else null
                    )
                }
            }

            // Active filters
            if (hasActiveFilters) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A5F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔍 ${viewModel.getFilteredExpenseCount()}",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "Clear Filters",
                            color = Color(0xFF64B5F6),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { viewModel.clearFilters() }
                        )
                    }
                }
            }

            // Member info
            if (membersInThisCollection.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👥 Members (${membersInThisCollection.size}): ",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            membersInThisCollection.take(4).forEach { member ->
                                MemberAvatar(member = member, size = 28)
                            }

                            if (membersInThisCollection.size > 4) {
                                Text(
                                    text = "+${membersInThisCollection.size - 4}",
                                    color = Color(0xFF4CAF50),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Error message
            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E1E1E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.clearErrorMessage() },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "⚠️ $errorMessage\n(Tap to dismiss)",
                        color = Color(0xFFFF6B6B),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Members warning
            if (membersInThisCollection.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E1E1E)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "⚠️ No members in this collection!\nPlease add members first to start tracking expenses.",
                        color = Color(0xFFFF6B6B),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Content area
            when {
                isLoading && expenses.isEmpty() -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Loading expenses...",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                expenses.isEmpty() -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (hasActiveFilters) "🔍" else "📝",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = if (hasActiveFilters) "No expenses match your search" else "No expenses yet!\nPull down to refresh or add your first expense.",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                else -> {
                    // UPDATED: Debt/Lending Summary Card (same as settlement screen)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { onSettlementClick() },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "💰 Financial Overview",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Tap for details →",
                                    color = Color(0xFF4CAF50),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Calculate debt summary from expenses
                            val memberBalances = viewModel.calculateMemberBalances(expenses, membersInThisCollection)
                            val totalOwed = memberBalances.filter { it.netBalance < 0 }.sumOf { -it.netBalance }
                            val totalToReceive = memberBalances.filter { it.netBalance > 0 }.sumOf { it.netBalance }
                            val peopleInDebt = memberBalances.count { it.netBalance < 0 }
                            val peopleToReceive = memberBalances.count { it.netBalance > 0 }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Total debt column
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "💸 Total Owed",
                                        color = Color(0xFFFF6B6B),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = AppUtils.formatCurrency(totalOwed),
                                        color = Color(0xFFFF6B6B),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (peopleInDebt > 0) {
                                        Text(
                                            text = "by $peopleInDebt people",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                // Total to receive column
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "💚 To Receive",
                                        color = Color(0xFF4CAF50),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = AppUtils.formatCurrency(totalToReceive),
                                        color = Color(0xFF4CAF50),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (peopleToReceive > 0) {
                                        Text(
                                            text = "by $peopleToReceive people",
                                            color = Color.Gray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }

                            // Quick balance status
                            Text(
                                text = when {
                                    totalOwed == 0.0 && totalToReceive == 0.0 -> "🎉 Everyone is settled!"
                                    totalOwed > totalToReceive -> "⚠️ More debt than lending"
                                    totalToReceive > totalOwed -> "✅ More lending than debt"
                                    else -> "⚖️ Debt and lending balanced"
                                },
                                color = when {
                                    totalOwed == 0.0 && totalToReceive == 0.0 -> Color(0xFF4CAF50)
                                    totalOwed > totalToReceive -> Color(0xFFFF6B6B)
                                    totalToReceive > totalOwed -> Color(0xFF4CAF50)
                                    else -> Color(0xFF64B5F6)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // Expense list
                    val groupedExpenses = viewModel.getGroupedExpenses()
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groupedExpenses.forEach { (dateGroup, expensesInGroup) ->
                            item {
                                Text(
                                    text = "📅 $dateGroup",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                                )
                            }

                            items(expensesInGroup, key = { it.id }) { expense ->
                                ExpenseCard(
                                    expense = expense,
                                    members = membersInThisCollection,
                                    onEditClick = { onEditExpenseClick(expense.id) }
                                )
                            }
                        }
                    }
                }
            }

            // FAB
            FloatingActionButton(
                onClick = onAddExpenseClick,
                modifier = Modifier.padding(top = 16.dp),
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            ) {
                Text("+ Add Expense", color = Color.White, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ExpenseCard(
    expense: com.puneet8goyal.splitkaro.data.Expense,
    members: List<com.puneet8goyal.splitkaro.data.Member>,
    onEditClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "💰 ${AppUtils.formatCurrency(expense.amount)}",
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "Paid by: ${members.find { it.id == expense.paidByMemberId }?.name ?: "Unknown"}",
                    color = Color(0xFFBBBBBB),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "Per person: ${AppUtils.formatCurrency(expense.perPersonAmount)} • Split ${expense.splitAmongMemberIds.size} ways",
                    color = Color(0xFFBBBBBB),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .background(Color(0xFF4CAF50), RoundedCornerShape(50))
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Expense",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
