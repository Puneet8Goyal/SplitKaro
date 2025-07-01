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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.ui.theme.ResponsiveSpacing
import com.puneet8goyal.splitkaro.utils.ModernPreviewCard
import com.puneet8goyal.splitkaro.utils.ModernTextField
import com.puneet8goyal.splitkaro.utils.PremiumStatusCard
import com.puneet8goyal.splitkaro.utils.ResponsiveButton
import com.puneet8goyal.splitkaro.utils.ResponsiveMemberSelector
import com.puneet8goyal.splitkaro.utils.StatusType
import com.puneet8goyal.splitkaro.viewmodel.AddExpenseViewModel
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    collectionId: Long,
    viewModel: AddExpenseViewModel = hiltViewModel(),
    collectionViewModel: ExpenseCollectionViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onExpenseAdded: (String) -> Unit
) {
    val description = viewModel.description
    val amount = viewModel.amount
    val paidByMemberId = viewModel.paidByMemberId
    val splitAmongMemberIds = viewModel.splitAmongMemberIds
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading

    val collectionMembers by collectionViewModel.collectionMembers.collectAsState()
    val membersInThisCollection = collectionMembers[collectionId] ?: emptyList()

    // RESPONSIVE: Screen configuration
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = ResponsiveSpacing.adaptiveHorizontal()

    // Preview calculations
    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val selectedMembersCount = splitAmongMemberIds.size
    val perPersonAmount = if (selectedMembersCount > 0) amountValue / selectedMembersCount else 0.0

    LaunchedEffect(collectionId) {
        collectionViewModel.loadMembersForCollection(collectionId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Error Message (Top Priority)
            AnimatedVisibility(
                visible = snackbarMessage.isNotEmpty(),
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
                        message = snackbarMessage,
                        type = StatusType.ERROR,
                        onDismiss = { viewModel.clearErrorMessage() },
                        autoDismiss = true,
                        autoDismissDelay = 4000L,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                }
            }

            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Add Expense",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.25).sp
                        ),
                        color = AppTheme.colors.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                membersInThisCollection.isEmpty() -> {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = horizontalPadding,
                                vertical = AppTheme.spacing.huge
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                        ) {
                            Text(
                                text = "👥",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 64.sp
                                )
                            )
                            Text(
                                text = "No members found",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.25).sp
                                ),
                                color = AppTheme.colors.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Please add members to the collection before creating expenses",
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
                            .padding(horizontal = horizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)
                    ) {
                        // Expense Details Section
                        Column(
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                        ) {
                            Text(
                                text = "Expense Details",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.25).sp
                                ),
                                color = AppTheme.colors.onSurface
                            )

                            ModernTextField(
                                value = description,
                                onValueChange = { viewModel.updateDescription(it) },
                                label = "Description",
                                placeholder = "What was this expense for?",
                                leadingIcon = Icons.Default.Description,
                                enabled = !isLoading,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences
                                )
                            )

                            ModernTextField(
                                value = amount,
                                onValueChange = { viewModel.updateAmount(it) },
                                label = "Amount",
                                placeholder = "0.00",
                                leadingIcon = Icons.Default.MonetizationOn,
                                enabled = !isLoading,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                )
                            )
                        }

                        // RESPONSIVE: Paid By Section
                        ResponsiveMemberSelector(
                            members = membersInThisCollection,
                            selectedMemberIds = listOfNotNull(paidByMemberId),
                            onMemberClick = { member -> viewModel.updatePaidByMemberId(member.id) },
                            selectionType = "radio",
                            label = "Who paid?"
                        )

                        // RESPONSIVE: Split Among Section
                        ResponsiveMemberSelector(
                            members = membersInThisCollection,
                            selectedMemberIds = splitAmongMemberIds,
                            onMemberClick = { member ->
                                val currentList = splitAmongMemberIds.toMutableList()
                                if (member.id in currentList) {
                                    currentList.remove(member.id)
                                } else {
                                    currentList.add(member.id)
                                }
                                viewModel.updateSplitAmongMemberIds(currentList)
                            },
                            selectionType = "checkbox",
                            label = "Split among"
                        )

                        // Preview Section
                        if (amountValue > 0 && selectedMembersCount > 0) {
                            ModernPreviewCard(
                                totalAmount = amountValue,
                                perPersonAmount = perPersonAmount,
                                memberCount = selectedMembersCount
                            )
                        }

                        // Add Button
                        ResponsiveButton(
                            text = if (isLoading) "Adding..." else "Add Expense",
                            onClick = {
                                viewModel.addExpense(collectionId) {
                                    onExpenseAdded("✅ Expense added successfully!")
                                }
                            },
                            enabled = !isLoading && description.trim().isNotEmpty() &&
                                    amount.isNotEmpty() && paidByMemberId != null &&
                                    splitAmongMemberIds.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Loading indicator
                        if (isLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    color = AppTheme.colors.primary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Bottom spacing
                        Spacer(modifier = Modifier.height(AppTheme.spacing.xl))
                    }
                }
            }
        }
    }
}
