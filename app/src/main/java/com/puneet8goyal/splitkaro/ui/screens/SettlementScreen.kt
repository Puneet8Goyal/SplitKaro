package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.utils.AppUtils
import com.puneet8goyal.splitkaro.utils.MemberAvatar
import com.puneet8goyal.splitkaro.utils.ModernLoadingState
import com.puneet8goyal.splitkaro.utils.PremiumStatusCard
import com.puneet8goyal.splitkaro.utils.StatusType
import com.puneet8goyal.splitkaro.viewmodel.SettlementViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementScreen(
    collectionId: Long,
    viewModel: SettlementViewModel = hiltViewModel(),
    onBackClick: (String?) -> Unit = { _ -> }
) {
    val expenses = viewModel.expenses
    val members = viewModel.members
    val memberBalances = viewModel.memberBalances
    val unsettledSettlements = viewModel.getUnsettledSettlements()
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val successMessage = viewModel.successMessage
    val settledCount = viewModel.getSettledCount()
    val allSettled = viewModel.areAllSettled()

    var isRefreshing by remember { mutableStateOf(false) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.refreshData(collectionId)
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top positioned success messages
                AnimatedVisibility(
                    visible = successMessage.isNotEmpty(),
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
                            message = successMessage,
                            type = StatusType.SUCCESS,
                            onDismiss = { viewModel.clearSuccessMessage() },
                            autoDismiss = true,
                            autoDismissDelay = 3000L,
                            modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                    }
                }

                // Top positioned error messages
                AnimatedVisibility(
                    visible = errorMessage.isNotEmpty(),
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
                            message = errorMessage,
                            type = StatusType.ERROR,
                            onDismiss = { viewModel.clearErrorMessage() },
                            autoDismiss = true,
                            autoDismissDelay = 4000L,
                            modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                    }
                }

                // Top App Bar
                TopAppBar(
                    title = {
                        Text(
                            text = "Settlement",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.25).sp
                            ),
                            color = AppTheme.colors.onSurface
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBackClick(null) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = AppTheme.colors.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppTheme.colors.background
                    )
                )

                // Content
                when {
                    isRefreshing && expenses.isEmpty() -> {
                        ModernLoadingState(message = "Refreshing settlement data...")
                    }

                    expenses.isEmpty() -> {
                        // Empty state
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(AppTheme.spacing.huge),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                            ) {
                                Text(
                                    text = "💰",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontSize = 64.sp
                                    )
                                )

                                Text(
                                    text = "No expenses to settle",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.25).sp
                                    ),
                                    color = AppTheme.colors.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Add some expenses to see settlement information",
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

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = AppTheme.spacing.xl),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)
                        ) {
                            // Summary Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = AppTheme.colors.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(AppTheme.radius.xl)
                            ) {
                                Column(
                                    modifier = Modifier.padding(AppTheme.spacing.xl),
                                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                                ) {
                                    Text(
                                        text = "Summary",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = (-0.25).sp
                                        ),
                                        color = AppTheme.colors.onSurface
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = "Total Amount",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = AppTheme.colors.onSurfaceVariant
                                            )
                                            Text(
                                                text = AppUtils.formatCurrency(viewModel.getTotalCollectionAmount()),
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = AppTheme.colors.onSurface
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "Total Expenses",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = AppTheme.colors.onSurfaceVariant
                                            )
                                            Text(
                                                text = "${expenses.size}",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = AppTheme.colors.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            // Member Balances
                            if (memberBalances.isNotEmpty()) {
                                Text(
                                    text = "Member Balances",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.25).sp
                                    ),
                                    color = AppTheme.colors.onSurface
                                )

                                memberBalances.forEach { balance ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = AppTheme.colors.surface
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        shape = RoundedCornerShape(AppTheme.radius.xl)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(AppTheme.spacing.xl),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                                        ) {
                                            MemberAvatar(
                                                member = balance.member,
                                                size = 48
                                            )

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = balance.member.name,
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.SemiBold,
                                                        letterSpacing = (-0.25).sp
                                                    ),
                                                    color = AppTheme.colors.onSurface
                                                )

                                                Text(
                                                    text = "Paid: ${AppUtils.formatCurrency(balance.totalPaid)} • Owes: ${
                                                        AppUtils.formatCurrency(
                                                            balance.totalOwed
                                                        )
                                                    }",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = AppTheme.colors.onSurfaceVariant
                                                )
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.End,
                                                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
                                            ) {
                                                Icon(
                                                    imageVector = when {
                                                        balance.netBalance > 0 -> Icons.Outlined.TrendingUp
                                                        balance.netBalance < 0 -> Icons.Outlined.TrendingDown
                                                        else -> Icons.Default.Check
                                                    },
                                                    contentDescription = null,
                                                    tint = when {
                                                        balance.netBalance > 0 -> AppTheme.colors.success
                                                        balance.netBalance < 0 -> AppTheme.colors.error
                                                        else -> AppTheme.colors.success
                                                    },
                                                    modifier = Modifier.size(16.dp)
                                                )

                                                Text(
                                                    text = when {
                                                        balance.netBalance > 0 -> AppUtils.formatCurrency(
                                                            balance.netBalance
                                                        )

                                                        balance.netBalance < 0 -> AppUtils.formatCurrency(
                                                            -balance.netBalance
                                                        )

                                                        else -> "Settled"
                                                    },
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = (-0.25).sp
                                                    ),
                                                    color = when {
                                                        balance.netBalance > 0 -> AppTheme.colors.success
                                                        balance.netBalance < 0 -> AppTheme.colors.error
                                                        else -> AppTheme.colors.success
                                                    }
                                                )

                                                Text(
                                                    text = when {
                                                        balance.netBalance > 0 -> "gets back"
                                                        balance.netBalance < 0 -> "owes"
                                                        else -> "✓"
                                                    },
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = when {
                                                        balance.netBalance > 0 -> AppTheme.colors.success
                                                        balance.netBalance < 0 -> AppTheme.colors.error
                                                        else -> AppTheme.colors.success
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Settlements
                            if (unsettledSettlements.isNotEmpty()) {
                                Text(
                                    text = "Pending Settlements",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.25).sp
                                    ),
                                    color = AppTheme.colors.onSurface
                                )

                                unsettledSettlements.forEach { settlementWithStatus ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = AppTheme.colors.surface
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        shape = RoundedCornerShape(AppTheme.radius.xl)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(AppTheme.spacing.xl),
                                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(
                                                    AppTheme.spacing.lg
                                                )
                                            ) {
                                                MemberAvatar(
                                                    member = settlementWithStatus.settlement.fromMember,
                                                    size = 40
                                                )

                                                Icon(
                                                    imageVector = Icons.Default.ArrowForward,
                                                    contentDescription = "pays",
                                                    tint = AppTheme.colors.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )

                                                MemberAvatar(
                                                    member = settlementWithStatus.settlement.toMember,
                                                    size = 40
                                                )

                                                Spacer(modifier = Modifier.weight(1f))

                                                Text(
                                                    text = AppUtils.formatCurrency(
                                                        settlementWithStatus.settlement.amount
                                                    ),
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = (-0.25).sp
                                                    ),
                                                    color = AppTheme.colors.primary
                                                )
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${settlementWithStatus.settlement.fromMember.name} pays ${settlementWithStatus.settlement.toMember.name}",
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Medium
                                                    ),
                                                    color = AppTheme.colors.onSurface,
                                                    modifier = Modifier.weight(1f)
                                                )

                                                // FIXED: Proper button logic with dynamic text and state
                                                Button(
                                                    onClick = {
                                                        viewModel.markSettlementAsSettled(
                                                            settlementWithStatus
                                                        ) {
                                                            // Pass success message back to HomeScreen
                                                            onBackClick("💰 Settlement completed successfully!")
                                                        }
                                                    },
                                                    enabled = !settlementWithStatus.isSettled,
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (settlementWithStatus.isSettled)
                                                            AppTheme.colors.success.copy(alpha = 0.6f)
                                                        else
                                                            AppTheme.colors.success,
                                                        contentColor = Color.White,
                                                        disabledContainerColor = AppTheme.colors.success.copy(
                                                            alpha = 0.6f
                                                        ),
                                                        disabledContentColor = Color.White
                                                    ),
                                                    shape = RoundedCornerShape(AppTheme.radius.md)
                                                ) {
                                                    Icon(
                                                        imageVector = if (settlementWithStatus.isSettled)
                                                            Icons.Default.Check
                                                        else
                                                            Icons.Default.ArrowForward,
                                                        contentDescription = if (settlementWithStatus.isSettled) "Settled" else "Mark as Settled",
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
                                                    Text(
                                                        text = if (settlementWithStatus.isSettled) "✓ Settled" else "Settle",
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // All Settled Message
                            if (allSettled || (unsettledSettlements.isEmpty() && settledCount > 0)) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = AppTheme.colors.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(AppTheme.radius.xl)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(AppTheme.spacing.huge),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                                    ) {
                                        Text(
                                            text = "🎉",
                                            style = MaterialTheme.typography.displayMedium.copy(
                                                fontSize = 48.sp
                                            )
                                        )
                                        Text(
                                            text = "All Settled!",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (-0.25).sp
                                            ),
                                            color = AppTheme.colors.success,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "No payments needed. Everyone is settled up!",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = AppTheme.colors.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            // Perfect Balance Message
                            if (unsettledSettlements.isEmpty() && settledCount == 0 && memberBalances.all { it.netBalance == 0.0 }) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = AppTheme.colors.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    shape = RoundedCornerShape(AppTheme.radius.xl)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(AppTheme.spacing.huge),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                                    ) {
                                        Text(
                                            text = "⚖️",
                                            style = MaterialTheme.typography.displayMedium.copy(
                                                fontSize = 48.sp
                                            )
                                        )
                                        Text(
                                            text = "Perfect Balance!",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (-0.25).sp
                                            ),
                                            color = AppTheme.colors.success,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Everyone has paid their exact share. No settlements needed!",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = AppTheme.colors.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(AppTheme.spacing.xl))
                        }
                    }
                }
            }
        }
    }
}
