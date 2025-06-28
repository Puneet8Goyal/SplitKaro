package com.puneet8goyal.splitkaro.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.utils.MemberAvatar
import com.puneet8goyal.splitkaro.utils.ModernLoadingState
import com.puneet8goyal.splitkaro.utils.PremiumStatusCard
import com.puneet8goyal.splitkaro.utils.StatusType
import com.puneet8goyal.splitkaro.viewmodel.ManageMembersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMembersScreen(
    collectionId: Long,
    viewModel: ManageMembersViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val collectionMembers by viewModel.collectionMembers.collectAsState()
    val allMembers by viewModel.allMembers.collectAsState()
    val collection = viewModel.collection
    val newMemberName = viewModel.newMemberName
    val snackbarMessage = viewModel.snackbarMessage
    val isLoading = viewModel.isLoading
    val showAddMemberDialog = viewModel.showAddMemberDialog
    val showRemoveDialog = viewModel.showRemoveDialog
    val memberToRemove = viewModel.memberToRemove
    val availableMembers = viewModel.getAvailableMembers()

    LaunchedEffect(collectionId) {
        viewModel.loadData(collectionId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top positioned success/error messages - EXACTLY like other screens
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
                        type = if (snackbarMessage.contains("success", ignoreCase = true) ||
                            snackbarMessage.contains("added", ignoreCase = true) ||
                            snackbarMessage.contains("removed", ignoreCase = true)
                        )
                            StatusType.SUCCESS else StatusType.ERROR,
                        onDismiss = { viewModel.clearMessage() },
                        autoDismiss = true,
                        autoDismissDelay = 3000L,
                        modifier = Modifier.padding(horizontal = AppTheme.spacing.xl)
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                }
            }

            // Top App Bar - EXACTLY like other screens
            TopAppBar(
                title = {
                    Text(
                        text = "Manage Members",
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

            // Content - EXACTLY like other screens
            when {
                isLoading -> {
                    ModernLoadingState(message = "Loading members...")
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = AppTheme.spacing.xl),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)
                    ) {
                        // Group Info - SAME style as other cards
                        collection?.let {
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
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = AppTheme.colors.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Column {
                                        Text(
                                            text = it.name,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = (-0.25).sp
                                            ),
                                            color = AppTheme.colors.onSurface
                                        )
                                        Text(
                                            text = "${collectionMembers.size} ${if (collectionMembers.size == 1) "member" else "members"}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = AppTheme.colors.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Current Members List - SAME style as expense cards
                        if (collectionMembers.isEmpty()) {
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
                                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                                ) {
                                    Text(
                                        text = "👥",
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontSize = 48.sp
                                        )
                                    )
                                    Text(
                                        text = "No members yet",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = AppTheme.colors.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Add some members to start splitting expenses!",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = AppTheme.colors.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            collectionMembers.forEach { member ->
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
                                            member = member,
                                            size = 48
                                        )

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = member.name,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                    letterSpacing = (-0.25).sp
                                                ),
                                                color = AppTheme.colors.onSurface
                                            )
                                            Text(
                                                text = "Member",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                color = AppTheme.colors.onSurfaceVariant
                                            )
                                        }

                                        if (collectionMembers.size > 1) {
                                            IconButton(
                                                onClick = { viewModel.openRemoveDialog(member) }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Remove Member",
                                                    tint = AppTheme.colors.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Available Members - SAME style
                        if (availableMembers.isNotEmpty()) {
                            Text(
                                text = "Add Existing Members",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.25).sp
                                ),
                                color = AppTheme.colors.onSurface
                            )

                            availableMembers.forEach { member ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.addExistingMember(member.id, collectionId)
                                        },
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
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = AppTheme.colors.success,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        MemberAvatar(
                                            member = member,
                                            size = 40
                                        )

                                        Text(
                                            text = member.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = (-0.25).sp
                                            ),
                                            color = AppTheme.colors.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Text(
                                            text = "Tap to add",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = AppTheme.colors.success
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp)) // Space for FAB
                    }
                }
            }
        }

        // Floating Action Button - EXACTLY like other screens
        ExtendedFloatingActionButton(
            onClick = { viewModel.openAddMemberDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(AppTheme.spacing.xl),
            containerColor = AppTheme.colors.primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(AppTheme.radius.lg)
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = "Add Member",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
            Text(
                "Add Member",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // Dialogs - EXACTLY like other screens
        if (showAddMemberDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeAddMemberDialog() },
                containerColor = AppTheme.colors.surface,
                shape = RoundedCornerShape(AppTheme.radius.xl),
                title = {
                    Text(
                        "Add New Member",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.25).sp
                        ),
                        color = AppTheme.colors.onSurface
                    )
                },
                text = {
                    OutlinedTextField(
                        value = newMemberName,
                        onValueChange = { viewModel.updateNewMemberName(it) },
                        label = {
                            Text(
                                "Member Name",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        },
                        placeholder = {
                            Text(
                                "Enter member name",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppTheme.colors.primary,
                            unfocusedBorderColor = AppTheme.colors.border,
                            focusedTextColor = AppTheme.colors.onSurface,
                            unfocusedTextColor = AppTheme.colors.onSurface,
                            cursorColor = AppTheme.colors.primary,
                            focusedLabelColor = AppTheme.colors.primary,
                            unfocusedLabelColor = AppTheme.colors.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(AppTheme.radius.md),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.15.sp
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addNewMember(collectionId)
                        },
                        enabled = newMemberName.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            "Add Member",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.closeAddMemberDialog() },
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            )
        }

        if (showRemoveDialog && memberToRemove != null) {
            AlertDialog(
                onDismissRequest = { viewModel.closeRemoveDialog() },
                containerColor = AppTheme.colors.surface,
                shape = RoundedCornerShape(AppTheme.radius.xl),
                title = {
                    Text(
                        "Remove Member",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = AppTheme.colors.onSurface
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to remove '${memberToRemove!!.name}' from this group? They will no longer be able to participate in expenses for this group.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = AppTheme.colors.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.removeMemberFromCollection(memberToRemove!!, collectionId)
                            viewModel.closeRemoveDialog()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.error,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            "Remove",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.closeRemoveDialog() },
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}
