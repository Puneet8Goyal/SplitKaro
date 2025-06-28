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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.utils.ModernErrorCard
import com.puneet8goyal.splitkaro.utils.ModernMemberCard
import com.puneet8goyal.splitkaro.utils.ModernMemberRow
import com.puneet8goyal.splitkaro.utils.ModernPreviewCard
import com.puneet8goyal.splitkaro.utils.ModernSectionCard
import com.puneet8goyal.splitkaro.utils.ModernTextField
import com.puneet8goyal.splitkaro.utils.PremiumStatusCard
import com.puneet8goyal.splitkaro.utils.StatusType
import com.puneet8goyal.splitkaro.viewmodel.AddExpenseViewModel
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: AddExpenseViewModel = hiltViewModel(),
    collectionViewModel: ExpenseCollectionViewModel = hiltViewModel(),
    collectionId: Long,
    onSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val description = viewModel.description
    val amount = viewModel.amount
    val paidByMemberId = viewModel.paidByMemberId
    val splitAmongMemberIds = viewModel.splitAmongMemberIds
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading

    val collectionMembers by collectionViewModel.collectionMembers.collectAsState()
    val membersInThisCollection = collectionMembers[collectionId] ?: emptyList()

    LaunchedEffect(collectionId) {
        collectionViewModel.loadMembersForCollection(collectionId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // FIXED: Top positioned error snackbar - ALWAYS VISIBLE
            AnimatedVisibility(
                visible = snackbarMessage.isNotEmpty() && !snackbarMessage.contains("success", ignoreCase = true),
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
                        modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                }
            }

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppTheme.spacing.xl),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)
            ) {
                when {
                    membersInThisCollection.isEmpty() -> {
                        ModernErrorCard(
                            title = "No members found",
                            message = "Please add members to this group first."
                        )
                        return@Column
                    }
                    membersInThisCollection.size < 2 -> {
                        ModernErrorCard(
                            title = "Need more members",
                            message = "You need at least 2 people in the group to split expenses. Please add more members first."
                        )
                        return@Column
                    }
                }

                ModernSectionCard(
                    title = "Expense Details",
                    icon = Icons.Outlined.Description
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                    ) {
                        ModernTextField(
                            value = description,
                            onValueChange = { viewModel.updateDescription(it) },
                            label = "Description",
                            placeholder = "e.g., Dinner at restaurant",
                            leadingIcon = Icons.Outlined.Description,
                            enabled = !isLoading,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            )
                        )

                        ModernTextField(
                            value = amount,
                            onValueChange = { viewModel.updateAmount(it) },
                            label = "Amount (₹)",
                            placeholder = "0.00",
                            leadingIcon = Icons.Outlined.AccountBalanceWallet,
                            enabled = !isLoading,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }

                ModernSectionCard(
                    title = "Who paid?",
                    subtitle = "Select the person who paid for this expense"
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                    ) {
                        items(membersInThisCollection) { member ->
                            ModernMemberCard(
                                member = member,
                                isSelected = member.id == paidByMemberId,
                                onClick = { viewModel.updatePaidByMemberId(member.id) },
                                selectionType = "radio"
                            )
                        }
                    }
                }

                ModernSectionCard(
                    title = "Split among",
                    subtitle = if (splitAmongMemberIds.isNotEmpty())
                        "${splitAmongMemberIds.size} selected"
                    else
                        "Select people to split this expense among"
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
                    ) {
                        membersInThisCollection.forEach { member ->
                            ModernMemberRow(
                                member = member,
                                isSelected = member.id in splitAmongMemberIds,
                                onClick = {
                                    val newList = splitAmongMemberIds.toMutableList()
                                    if (member.id in newList) {
                                        newList.remove(member.id)
                                    } else {
                                        newList.add(member.id)
                                    }
                                    viewModel.updateSplitAmongMemberIds(newList)
                                }
                            )
                        }
                    }
                }

                if (splitAmongMemberIds.isNotEmpty() && amount.toDoubleOrNull() != null) {
                    val amountValue = amount.toDouble()
                    val perPerson = amountValue / splitAmongMemberIds.size

                    ModernPreviewCard(
                        totalAmount = amountValue,
                        perPersonAmount = perPerson,
                        memberCount = splitAmongMemberIds.size
                    )
                }

                Button(
                    onClick = {
                        viewModel.addExpense(
                            collectionId = collectionId,
                            onSuccess = onSuccess
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading && membersInThisCollection.size >= 2,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.primary,
                        contentColor = Color.White,
                        disabledContainerColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.12f),
                        disabledContentColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.38f)
                    ),
                    shape = RoundedCornerShape(AppTheme.radius.lg),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                        Text("Adding...")
                    } else {
                        Text(
                            "Add Expense",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.1.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.spacing.xl))
            }
        }
    }
}
