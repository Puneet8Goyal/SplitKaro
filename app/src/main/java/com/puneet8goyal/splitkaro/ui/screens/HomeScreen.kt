package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.utils.AppUtils
import com.puneet8goyal.splitkaro.utils.ModernLoadingState
import com.puneet8goyal.splitkaro.utils.ModernSearchBar
import com.puneet8goyal.splitkaro.utils.PremiumStatusCard
import com.puneet8goyal.splitkaro.utils.StatusType
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel
import com.puneet8goyal.splitkaro.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    collectionViewModel: ExpenseCollectionViewModel = hiltViewModel(),
    collectionId: Long,
    initialSuccessMessage: String? = null,
    initialErrorMessage: String? = null,
    onAddExpenseClick: () -> Unit,
    onEditExpenseClick: (Long) -> Unit,
    onSettlementClick: () -> Unit,
    onManageMembersClick: () -> Unit
) {
    val expenses = viewModel.expenses
    val isLoading = viewModel.isLoading
    val isRefreshing = viewModel.isRefreshing
    val errorMessage = viewModel.errorMessage
    val collectionMembers by collectionViewModel.collectionMembers.collectAsState()
    val membersInThisCollection = collectionMembers[collectionId] ?: emptyList()

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    // Navigation message state
    var showNavigationSuccess by remember { mutableStateOf(initialSuccessMessage != null) }
    var showNavigationError by remember { mutableStateOf(initialErrorMessage != null) }
    var navigationSuccessMessage by remember { mutableStateOf(initialSuccessMessage ?: "") }
    var navigationErrorMessage by remember { mutableStateOf(initialErrorMessage ?: "") }

    // Filter expenses based on search
    val filteredExpenses = remember(expenses, searchQuery, membersInThisCollection) {
        if (searchQuery.isEmpty()) {
            expenses
        } else {
            AppUtils.filterExpenses(expenses, searchQuery, membersInThisCollection)
        }
    }

    LaunchedEffect(collectionId) {
        viewModel.loadExpenses(collectionId)
        collectionViewModel.loadMembersForCollection(collectionId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshExpenses(collectionId) },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Navigation Success Message (Top Priority)
                AnimatedVisibility(
                    visible = showNavigationSuccess && navigationSuccessMessage.isNotEmpty(),
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(400)
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(300)
                    ) + fadeOut()
                ) {
                    Column {
                        PremiumStatusCard(
                            message = navigationSuccessMessage,
                            type = StatusType.SUCCESS,
                            onDismiss = {
                                showNavigationSuccess = false
                                navigationSuccessMessage = ""
                            },
                            autoDismiss = true,
                            autoDismissDelay = 4000L,
                            modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                    }
                }

                // Navigation Error Message
                AnimatedVisibility(
                    visible = showNavigationError && navigationErrorMessage.isNotEmpty(),
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(400)
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(300)
                    ) + fadeOut()
                ) {
                    Column {
                        PremiumStatusCard(
                            message = navigationErrorMessage,
                            type = StatusType.ERROR,
                            onDismiss = {
                                showNavigationError = false
                                navigationErrorMessage = ""
                            },
                            autoDismiss = true,
                            autoDismissDelay = 5000L,
                            modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                    }
                }

                // Modern Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.spacing.xl, vertical = AppTheme.spacing.lg),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Expenses",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = AppTheme.colors.onSurface
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                    ) {
                        ModernIconButton(
                            onClick = onManageMembersClick,
                            icon = Icons.Default.Person,
                            contentDescription = "Members"
                        )
                        ModernIconButton(
                            onClick = { showSearchBar = !showSearchBar },
                            icon = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }

                // Search Bar
                AnimatedVisibility(
                    visible = showSearchBar,
                    enter = slideInVertically(animationSpec = tween(300)) + fadeIn(),
                    exit = slideOutVertically(animationSpec = tween(300)) + fadeOut()
                ) {
                    Column {
                        ModernSearchBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            onSearchClear = {
                                searchQuery = ""
                                showSearchBar = false
                            },
                            modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.lg))
                    }
                }

                // Add Members Recommendation when only current user exists
                if (membersInThisCollection.size <= 1) {
                    AddMembersRecommendationCard(
                        onAddMembersClick = onManageMembersClick,
                        modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.lg))
                }

                // Modern Balance Overview (only show if there are multiple members)
                if (membersInThisCollection.size > 1) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.spacing.xl)
                            .clickable { onSettlementClick() },
                        colors = CardDefaults.cardColors(
                            containerColor = AppTheme.colors.surface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 8.dp
                        ),
                        shape = RoundedCornerShape(AppTheme.radius.xl),
                        border = BorderStroke(1.dp, AppTheme.colors.border)
                    ) {
                        Column(
                            modifier = Modifier.padding(AppTheme.spacing.xl),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                        ) {
                            // Calculate user-centric balances
                            val userCentricBalances = viewModel.calculateUserCentricBalances(
                                expenses, membersInThisCollection
                            )
                            val overallBalance = viewModel.calculateCurrentUserOverallBalance(
                                expenses, membersInThisCollection
                            )

                            // Overall status with icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                            ) {
                                Icon(
                                    imageVector = when {
                                        overallBalance > 0 -> Icons.Outlined.TrendingUp
                                        overallBalance < 0 -> Icons.Outlined.TrendingDown
                                        else -> Icons.Outlined.Groups
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        overallBalance > 0 -> AppTheme.colors.success
                                        overallBalance < 0 -> AppTheme.colors.error
                                        else -> AppTheme.colors.onSurface
                                    },
                                    modifier = Modifier.size(24.dp)
                                )

                                Text(
                                    text = when {
                                        overallBalance > 0 -> "You are owed ${AppUtils.formatCurrency(overallBalance)} overall"
                                        overallBalance < 0 -> "You owe ${AppUtils.formatCurrency(-overallBalance)} overall"
                                        else -> "You are settled up"
                                    },
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.25).sp
                                    ),
                                    color = when {
                                        overallBalance > 0 -> AppTheme.colors.success
                                        overallBalance < 0 -> AppTheme.colors.error
                                        else -> AppTheme.colors.onSurface
                                    }
                                )
                            }

                            // Individual member balances
                            if (userCentricBalances.isNotEmpty()) {
                                Divider(color = AppTheme.colors.border.copy(alpha = 0.5f))

                                Text(
                                    text = "Individual Balances",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = AppTheme.colors.onSurfaceVariant
                                )

                                userCentricBalances.forEach { balance ->
                                    ModernBalanceRow(
                                        label = balance.member.name,
                                        amount = kotlin.math.abs(balance.amountOwedToUser),
                                        isPositive = balance.isPositive
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.spacing.xl))
                }

                // Local Error message (lower priority than navigation messages)
                AnimatedVisibility(
                    visible = errorMessage.isNotEmpty(),
                    enter = slideInVertically(animationSpec = tween(300)) + fadeIn(),
                    exit = slideOutVertically(animationSpec = tween(300)) + fadeOut()
                ) {
                    PremiumStatusCard(
                        message = errorMessage,
                        type = StatusType.ERROR,
                        onDismiss = { viewModel.clearErrorMessage() },
                        modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                    )
                }

                // Modern Expense List
                when {
                    isLoading && expenses.isEmpty() -> {
                        ModernLoadingState(message = "Loading expenses...")
                    }
                    filteredExpenses.isEmpty() && searchQuery.isNotEmpty() -> {
                        ModernEmptyExpenseState(hasActiveFilters = true)
                    }
                    filteredExpenses.isEmpty() -> {
                        ModernEmptyExpenseState(hasActiveFilters = false)
                    }
                    else -> {
                        val groupedExpenses = AppUtils.groupExpensesByDate(filteredExpenses)
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                            contentPadding = PaddingValues(
                                start = AppTheme.spacing.xl,
                                end = AppTheme.spacing.xl,
                                bottom = 100.dp
                            )
                        ) {
                            groupedExpenses.forEach { (dateGroup, expensesInGroup) ->
                                item {
                                    ModernSectionHeader(
                                        title = dateGroup,
                                        modifier = Modifier.padding(vertical = AppTheme.spacing.sm)
                                    )
                                }

                                items(expensesInGroup, key = { it.id }) { expense ->
                                    ModernExpenseCard(
                                        expense = expense,
                                        members = membersInThisCollection,
                                        currentUserId = viewModel.getCurrentUserId(),
                                        onEditClick = { onEditExpenseClick(expense.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Modern Extended Floating Action Button (only show if there are multiple members)
        if (membersInThisCollection.size > 1) {
            ExtendedFloatingActionButton(
                onClick = onAddExpenseClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(AppTheme.spacing.xl),
                containerColor = AppTheme.colors.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(AppTheme.radius.lg)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                Text(
                    "Add Expense",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun AddMembersRecommendationCard(
    onAddMembersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(AppTheme.radius.lg),
        border = BorderStroke(1.dp, AppTheme.colors.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            AppTheme.colors.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(AppTheme.radius.md)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        tint = AppTheme.colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "Add Members to Get Started",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.25).sp
                        ),
                        color = AppTheme.colors.onSurface
                    )

                    Text(
                        text = "You need at least 2 people to split expenses",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onAddMembersClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(AppTheme.radius.md),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                Text(
                    "Add Members",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun ModernIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(
                AppTheme.colors.surfaceContainer,
                RoundedCornerShape(AppTheme.radius.md)
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ModernBalanceRow(
    label: String,
    amount: Double,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isPositive) "$label owes you" else "You owe $label",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = AppTheme.colors.onSurfaceVariant
        )

        Text(
            text = AppUtils.formatCurrency(amount),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.25).sp
            ),
            color = if (isPositive) AppTheme.colors.success else AppTheme.colors.error
        )
    }
}

@Composable
fun ModernExpenseCard(
    expense: com.puneet8goyal.splitkaro.data.Expense,
    members: List<com.puneet8goyal.splitkaro.data.Member>,
    currentUserId: Long,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 4.dp
        ),
        shape = RoundedCornerShape(AppTheme.radius.lg),
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Row(
            modifier = Modifier.padding(AppTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AppTheme.colors.primaryContainer,
                                AppTheme.colors.primaryContainer.copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = null,
                    tint = AppTheme.colors.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.25).sp
                    ),
                    color = AppTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${members.find { it.id == expense.paidByMemberId }?.name ?: "Unknown"} paid ${AppUtils.formatCurrency(expense.amount)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = AppTheme.colors.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                val isPaidByCurrentUser = expense.paidByMemberId == currentUserId
                val isCurrentUserInSplit = currentUserId in expense.splitAmongMemberIds

                when {
                    isPaidByCurrentUser && isCurrentUserInSplit -> {
                        Text(
                            text = "you lent",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.success,
                            fontSize = 10.sp
                        )
                        Text(
                            text = AppUtils.formatCurrency(expense.amount - expense.perPersonAmount),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.25).sp
                            ),
                            color = AppTheme.colors.success
                        )
                    }
                    !isPaidByCurrentUser && isCurrentUserInSplit -> {
                        Text(
                            text = "you borrowed",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.error,
                            fontSize = 10.sp
                        )
                        Text(
                            text = AppUtils.formatCurrency(expense.perPersonAmount),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.25).sp
                            ),
                            color = AppTheme.colors.error
                        )
                    }
                    isPaidByCurrentUser && !isCurrentUserInSplit -> {
                        Text(
                            text = "you paid",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Text(
                            text = AppUtils.formatCurrency(expense.amount),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.25).sp
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernEmptyExpenseState(hasActiveFilters: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.huge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
        ) {
            Text(
                text = if (hasActiveFilters) "🔍" else "💰",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 64.sp
                )
            )

            Text(
                text = if (hasActiveFilters) "No matching expenses" else "No expenses yet",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.25).sp
                ),
                color = AppTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (hasActiveFilters)
                    "Try adjusting your search criteria"
                else
                    "Add your first expense to get started",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp
                ),
                color = AppTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        ),
        color = AppTheme.colors.onSurfaceVariant,
        modifier = modifier
    )
}
