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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.R
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
    val context = LocalContext.current

    // ViewModel state
    val description = viewModel.description
    val amount = viewModel.amount
    val paidByMemberId = viewModel.paidByMemberId
    val splitAmongMemberIds = viewModel.splitAmongMemberIds
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading

    // Collection members
    val collectionMembers by collectionViewModel.collectionMembers.collectAsState()
    val membersInThisCollection = collectionMembers[collectionId] ?: emptyList()

    // Screen configuration
    val horizontalPadding = ResponsiveSpacing.adaptiveHorizontal()

    // Preview calculations
    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val selectedMembersCount = splitAmongMemberIds.size
    val perPersonAmount = if (selectedMembersCount > 0) amountValue / selectedMembersCount else 0.0

    // Load members for this collection
    LaunchedEffect(collectionId) {
        collectionViewModel.loadMembersForCollection(collectionId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Error Message
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
                        text = stringResource(R.string.add_expense),
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
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.back),
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
                                style = MaterialTheme.typography.displayMedium.copy(fontSize = 64.sp)
                            )
                            Text(
                                text = stringResource(R.string.no_members_yet),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.25).sp
                                ),
                                color = AppTheme.colors.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(R.string.add_members_to_start),
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
                        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)) {
                            Text(
                                text = stringResource(R.string.expense_details),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.25).sp
                                ),
                                color = AppTheme.colors.onSurface
                            )

                            ModernTextField(
                                value = description,
                                onValueChange = { viewModel.updateDescription(it) },
                                label = stringResource(R.string.expense_description),
                                placeholder = stringResource(R.string.expense_description_hint),
                                leadingIcon = Icons.Default.Description,
                                enabled = !isLoading,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences
                                )
                            )

                            ModernTextField(
                                value = amount,
                                onValueChange = { viewModel.updateAmount(it) },
                                label = stringResource(R.string.amount),
                                placeholder = stringResource(R.string.amount_hint),
                                leadingIcon = Icons.Default.MonetizationOn,
                                enabled = !isLoading,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }

                        // Who Paid Section
                        ResponsiveMemberSelector(
                            members = membersInThisCollection,
                            selectedMemberIds = listOfNotNull(paidByMemberId),
                            onMemberClick = { member -> viewModel.updatePaidByMemberId(member.id) },
                            selectionType = "radio",
                            label = stringResource(R.string.who_paid)
                        )

                        // Split Among Section
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
                            label = stringResource(R.string.split_among)
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
                            text = if (isLoading) stringResource(R.string.adding) else stringResource(
                                R.string.add_expense
                            ),
                            onClick = {
                                viewModel.addExpense(collectionId) {
                                    onExpenseAdded(context.getString(R.string.expense_added_success))
                                }
                            },
                            enabled = !isLoading && description.trim().isNotEmpty() &&
                                    amount.isNotEmpty() && paidByMemberId != null &&
                                    splitAmongMemberIds.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Bottom spacing
                        Spacer(modifier = Modifier.height(AppTheme.spacing.xl))
                    }
                }
            }
        }
    }
}
