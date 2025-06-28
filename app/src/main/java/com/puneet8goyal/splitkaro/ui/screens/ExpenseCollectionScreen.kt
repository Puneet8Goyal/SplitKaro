package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.puneet8goyal.splitkaro.domain.ExpenseCalculator
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.utils.AppUtils
import com.puneet8goyal.splitkaro.utils.MemberAvatar
import com.puneet8goyal.splitkaro.utils.ModernBalanceBadge
import com.puneet8goyal.splitkaro.utils.ModernDebtRow
import com.puneet8goyal.splitkaro.utils.ModernLoadingState
import com.puneet8goyal.splitkaro.utils.PremiumStatusCard
import com.puneet8goyal.splitkaro.utils.StatusType
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel
import com.puneet8goyal.splitkaro.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCollectionScreen(
    viewModel: ExpenseCollectionViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    initialSuccessMessage: String? = null,
    initialErrorMessage: String? = null,
    onCollectionClick: (Long) -> Unit
) {
    val collections by viewModel.collections.collectAsState()
    val collectionMembers by viewModel.collectionMembers.collectAsState()
    val newCollectionName = viewModel.newCollectionName
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading
    val showCollectionDialog = viewModel.showCollectionDialog
    val isRefreshing = viewModel.isRefreshing // FIXED: Use ViewModel refresh state

    var collectionDebts by remember { mutableStateOf<Map<Long, CollectionDebtInfo>>(emptyMap()) }
    val scope = rememberCoroutineScope()
    val expenseCalculator = remember { ExpenseCalculator() }

    // Navigation message state
    var showNavigationSuccess by remember { mutableStateOf(initialSuccessMessage != null) }
    var showNavigationError by remember { mutableStateOf(initialErrorMessage != null) }
    var navigationSuccessMessage by remember { mutableStateOf(initialSuccessMessage ?: "") }
    var navigationErrorMessage by remember { mutableStateOf(initialErrorMessage ?: "") }

    // FIXED: Refresh data when screen comes back into focus (after settlement, etc.)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshCollections()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Initial load
    LaunchedEffect(Unit) {
        viewModel.loadCollections()
    }

    // Recalculate balances when collections or members change
    LaunchedEffect(collections, collectionMembers) {
        scope.launch {
            val debtMap = mutableMapOf<Long, CollectionDebtInfo>()
            val currentUserId = homeViewModel.getCurrentUserId()

            collections.forEach { collection ->
                homeViewModel.loadExpenses(collection.id)
                val expenses = homeViewModel.expenses
                val members = collectionMembers[collection.id] ?: emptyList()

                if (expenses.isNotEmpty() && members.isNotEmpty() && currentUserId != -1L) {
                    val userCentricBalances = expenseCalculator.calculateUserCentricBalances(
                        expenses, members, currentUserId
                    )

                    val totalToReceive = userCentricBalances
                        .filter { it.isPositive }
                        .sumOf { it.amountOwedToUser }
                    val totalOwed = userCentricBalances
                        .filter { !it.isPositive }
                        .sumOf { kotlin.math.abs(it.amountOwedToUser) }
                    val netBalance = totalToReceive - totalOwed

                    debtMap[collection.id] = CollectionDebtInfo(
                        totalOwed = totalOwed,
                        totalToReceive = totalToReceive,
                        netBalance = netBalance,
                        topDebtor = userCentricBalances.filter { !it.isPositive }
                            .maxByOrNull { kotlin.math.abs(it.amountOwedToUser) }?.member?.name,
                        topCreditor = userCentricBalances.filter { it.isPositive }
                            .maxByOrNull { it.amountOwedToUser }?.member?.name
                    )
                } else {
                    debtMap[collection.id] = CollectionDebtInfo(
                        totalOwed = 0.0,
                        totalToReceive = 0.0,
                        netBalance = 0.0,
                        topDebtor = null,
                        topCreditor = null
                    )
                }
            }
            collectionDebts = debtMap
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                // FIXED: Use ViewModel refresh method
                viewModel.refreshCollections()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
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
                            autoDismissDelay = 4000L
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
                            autoDismissDelay = 5000L
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                    }
                }

                // Modern Header with calculations
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Groups",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = AppTheme.colors.onSurface
                    )

                    if (collections.isNotEmpty()) {
                        val totalBalance = collectionDebts.values.sumOf { it.netBalance }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (totalBalance > 0) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown,
                                contentDescription = null,
                                tint = if (totalBalance > 0) AppTheme.colors.success else AppTheme.colors.error,
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = when {
                                    totalBalance > 0 -> "You are owed ${
                                        AppUtils.formatCurrency(
                                            totalBalance
                                        )
                                    } overall"

                                    totalBalance < 0 -> "You owe ${
                                        AppUtils.formatCurrency(
                                            kotlin.math.abs(
                                                totalBalance
                                            )
                                        )
                                    } overall"

                                    else -> "You are settled up across all groups"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = (-0.25).sp
                                ),
                                color = when {
                                    totalBalance > 0 -> AppTheme.colors.success
                                    totalBalance < 0 -> AppTheme.colors.error
                                    else -> AppTheme.colors.onSurface
                                }
                            )
                        }
                    } else {
                        Text(
                            text = "Create your first expense group",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Content
                when {
                    isLoading && collections.isEmpty() -> {
                        ModernLoadingState(message = "Loading groups...")
                    }

                    collections.isEmpty() -> {
                        GroupsEmptyState(
                            onCreateClick = { viewModel.openCollectionDialog() }
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(collections, key = { it.id }) { collection ->
                                val membersInCollection =
                                    collectionMembers[collection.id] ?: emptyList()
                                val debtInfo = collectionDebts[collection.id]
                                ModernCollectionCard(
                                    collection = collection,
                                    members = membersInCollection,
                                    debtInfo = debtInfo,
                                    currentUserName = homeViewModel.getCurrentUserName(),
                                    onCollectionClick = { onCollectionClick(collection.id) }
                                )
                            }
                        }
                    }
                }

                // Success/Error Message (Local messages)
                AnimatedVisibility(
                    visible = snackbarMessage.isNotEmpty(),
                    enter = slideInVertically(animationSpec = tween(300)) + fadeIn(),
                    exit = slideOutVertically(animationSpec = tween(300)) + fadeOut()
                ) {
                    PremiumStatusCard(
                        message = snackbarMessage,
                        type = if (snackbarMessage.contains("success", ignoreCase = true) ||
                            snackbarMessage.contains("created", ignoreCase = true)
                        )
                            StatusType.SUCCESS else StatusType.ERROR,
                        onDismiss = { viewModel.clearMessage() },
                        autoDismiss = true
                    )
                }
            }
        }

        // Modern Extended Floating Action Button
        if (collections.isNotEmpty()) {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openCollectionDialog() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = AppTheme.colors.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Group",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "New Group",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.sp
                    )
                )
            }
        }

        // Dialog
        if (showCollectionDialog) {
            ModernCreateGroupDialog(
                groupName = newCollectionName,
                onGroupNameChange = { viewModel.updateNewCollectionName(it) },
                onCreateClick = { viewModel.createCollection() },
                onDismissClick = { viewModel.closeCollectionDialog() },
                isLoading = isLoading
            )
        }
    }
}

// Data class for debt information
data class CollectionDebtInfo(
    val totalOwed: Double,
    val totalToReceive: Double,
    val netBalance: Double,
    val topDebtor: String?,
    val topCreditor: String?
)

@Composable
fun ModernCollectionCard(
    collection: com.puneet8goyal.splitkaro.data.ExpenseCollection,
    members: List<com.puneet8goyal.splitkaro.data.Member>,
    debtInfo: CollectionDebtInfo?,
    currentUserName: String?,
    onCollectionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCollectionClick() },
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Group icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    when (collection.name.hashCode() % 4) {
                                        0 -> AppTheme.colors.accent1
                                        1 -> AppTheme.colors.accent2
                                        2 -> AppTheme.colors.accent3
                                        else -> AppTheme.colors.accent4
                                    },
                                    when (collection.name.hashCode() % 4) {
                                        0 -> AppTheme.colors.accent1.copy(alpha = 0.8f)
                                        1 -> AppTheme.colors.accent2.copy(alpha = 0.8f)
                                        2 -> AppTheme.colors.accent3.copy(alpha = 0.8f)
                                        else -> AppTheme.colors.accent4.copy(alpha = 0.8f)
                                    }
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = collection.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.25).sp
                        ),
                        color = AppTheme.colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (members.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            Text(
                                text = "${members.size} ${if (members.size == 1) "member" else "members"}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.1.sp
                                ),
                                color = AppTheme.colors.onSurfaceVariant
                            )

                            // Member avatars
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-8).dp)
                            ) {
                                members.take(4).forEachIndexed { index, member ->
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .zIndex((4 - index).toFloat())
                                            .border(
                                                2.dp,
                                                AppTheme.colors.surface,
                                                CircleShape
                                            )
                                    ) {
                                        MemberAvatar(
                                            member = member,
                                            size = 28
                                        )
                                    }
                                }

                                if (members.size > 4) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                AppTheme.colors.primary.copy(alpha = 0.1f),
                                                CircleShape
                                            )
                                            .border(
                                                2.dp,
                                                AppTheme.colors.surface,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "+${members.size - 4}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = AppTheme.colors.primary,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Balance indicator
                if (debtInfo != null && debtInfo.netBalance != 0.0) {
                    ModernBalanceBadge(
                        amount = debtInfo.netBalance,
                        isPositive = debtInfo.netBalance > 0
                    )
                }
            }

            // Debt summary - only show others, not current user
            if (debtInfo != null && (debtInfo.totalOwed > 0 || debtInfo.totalToReceive > 0)) {
                Divider(
                    color = AppTheme.colors.border.copy(alpha = 0.5f),
                    thickness = 1.dp
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (debtInfo.totalToReceive > 0) {
                        ModernDebtRow(
                            label = if (debtInfo.topCreditor != null && debtInfo.topCreditor != currentUserName)
                                "${debtInfo.topCreditor} owes you"
                            else
                                "You are owed",
                            amount = debtInfo.totalToReceive,
                            isPositive = true
                        )
                    }

                    if (debtInfo.totalOwed > 0) {
                        ModernDebtRow(
                            label = if (debtInfo.topDebtor != null && debtInfo.topDebtor != currentUserName)
                                "You owe ${debtInfo.topDebtor}"
                            else
                                "You owe",
                            amount = debtInfo.totalOwed,
                            isPositive = false
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircle,
                        contentDescription = null,
                        tint = AppTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = "No expenses yet",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.1.sp
                        ),
                        color = AppTheme.colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun GroupsEmptyState(
    onCreateClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AppTheme.colors.primary.copy(alpha = 0.1f),
                                AppTheme.colors.accent1.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👥",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 48.sp
                    )
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "No groups yet",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.25).sp
                    ),
                    color = AppTheme.colors.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Create your first group to start splitting expenses with friends, family, or roommates.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.15.sp,
                        lineHeight = 24.sp
                    ),
                    color = AppTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        "Create Your First Group",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.1.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ModernCreateGroupDialog(
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    onCreateClick: () -> Unit,
    onDismissClick: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismissClick,
        containerColor = AppTheme.colors.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                "Create New Group",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.25).sp
                ),
                color = AppTheme.colors.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "Choose a name for your expense group",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.15.sp
                    ),
                    color = AppTheme.colors.onSurfaceVariant
                )

                OutlinedTextField(
                    value = groupName,
                    onValueChange = onGroupNameChange,
                    label = {
                        Text(
                            "Group Name",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    },
                    placeholder = {
                        Text(
                            "e.g., 'Trip to Goa', 'Flatmates'",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppTheme.colors.primary,
                        unfocusedBorderColor = AppTheme.colors.border,
                        focusedTextColor = AppTheme.colors.onSurface,
                        unfocusedTextColor = AppTheme.colors.onSurface,
                        cursorColor = AppTheme.colors.primary,
                        focusedLabelColor = AppTheme.colors.primary,
                        unfocusedLabelColor = AppTheme.colors.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.15.sp
                    ),
                    // FIXED: Add proper capitalization
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onCreateClick,
                enabled = groupName.trim().isNotEmpty() && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primary,
                    contentColor = Color.White,
                    disabledContainerColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.12f),
                    disabledContentColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.38f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        "Create",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.1.sp
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClick,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.1.sp
                    ),
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
        }
    )
}