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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneet8goyal.splitkaro.data.ExpenseCollection
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import com.puneet8goyal.splitkaro.ui.theme.ResponsiveSpacing
import com.puneet8goyal.splitkaro.utils.ModernLoadingState
import com.puneet8goyal.splitkaro.utils.PremiumStatusCard
import com.puneet8goyal.splitkaro.utils.ResponsiveCard
import com.puneet8goyal.splitkaro.utils.ResponsiveFloatingActionButton
import com.puneet8goyal.splitkaro.utils.ResponsiveMemberAvatarDisplay
import com.puneet8goyal.splitkaro.utils.ResponsiveMemberCountText
import com.puneet8goyal.splitkaro.utils.StatusType
import com.puneet8goyal.splitkaro.viewmodel.ExpenseCollectionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCollectionScreen(
    viewModel: ExpenseCollectionViewModel = hiltViewModel(),
    initialSuccessMessage: String? = null,
    initialErrorMessage: String? = null,
    onCollectionClick: (Long) -> Unit
) {
    // FIXED: Correct property access using StateFlow
    val collections by viewModel.collections.collectAsState()
    val collectionMembers by viewModel.collectionMembers.collectAsState()

    // FIXED: Direct property access (not function calls)
    val isLoading = viewModel.isLoading
    val isRefreshing = viewModel.isRefreshing
    val snackbarMessage = viewModel.snackbarMessage
    val showCollectionDialog = viewModel.showCollectionDialog
    val showMemberDialog = viewModel.showMemberDialog
    val newCollectionName = viewModel.newCollectionName
    val newMemberName = viewModel.newMemberName
    val currentCollectionId = viewModel.currentCollectionId

    // RESPONSIVE: Screen configuration
    val configuration = LocalConfiguration.current
    val horizontalPadding = ResponsiveSpacing.adaptiveHorizontal()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var collectionToDelete by remember { mutableStateOf<ExpenseCollection?>(null) }

    // Navigation message state
    var showNavigationSuccess by remember { mutableStateOf(initialSuccessMessage != null) }
    var showNavigationError by remember { mutableStateOf(initialErrorMessage != null) }
    var navigationSuccessMessage by remember { mutableStateOf(initialSuccessMessage ?: "") }
    var navigationErrorMessage by remember { mutableStateOf(initialErrorMessage ?: "") }

    // Clear navigation messages after they're shown
    LaunchedEffect(initialSuccessMessage, initialErrorMessage) {
        if (initialSuccessMessage != null) {
            delay(4000)
            showNavigationSuccess = false
            navigationSuccessMessage = ""
        }
        if (initialErrorMessage != null) {
            delay(5000)
            showNavigationError = false
            navigationErrorMessage = ""
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCollections()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .systemBarsPadding()
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshCollections() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Navigation Success Message
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
                            modifier = Modifier.padding(horizontal = horizontalPadding)
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
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                    }
                }

                // Local Status Messages
                AnimatedVisibility(
                    visible = snackbarMessage.isNotEmpty() &&
                            !showNavigationSuccess &&
                            !showNavigationError,
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
                                snackbarMessage.contains("created", ignoreCase = true) ||
                                snackbarMessage.contains("deleted", ignoreCase = true)
                            ) StatusType.SUCCESS else StatusType.ERROR,
                            onDismiss = { viewModel.clearMessage() },
                            autoDismiss = true,
                            autoDismissDelay = 3000L,
                            modifier = Modifier.padding(horizontal = horizontalPadding)
                        )
                        Spacer(modifier = Modifier.height(AppTheme.spacing.md))
                    }
                }

                // Top App Bar
                TopAppBar(
                    title = {
                        Text(
                            text = "SplitKaro",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = AppTheme.colors.onSurface
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppTheme.colors.background
                    )
                )

                // Content
                when {
                    isLoading && collections.isEmpty() -> {
                        ModernLoadingState(message = "Loading collections...")
                    }

                    collections.isEmpty() -> {
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
                                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)
                            ) {
                                Text(
                                    text = "🏠",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontSize = 80.sp
                                    )
                                )
                                Text(
                                    text = "Welcome to SplitKaro!",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = AppTheme.colors.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Create your first collection to start splitting expenses with friends and family",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 26.sp
                                    ),
                                    color = AppTheme.colors.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                horizontal = horizontalPadding,
                                vertical = AppTheme.spacing.lg
                            ),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                        ) {
                            item {
                                Text(
                                    text = "Your Collections",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = (-0.25).sp
                                    ),
                                    color = AppTheme.colors.onSurface,
                                    modifier = Modifier.padding(bottom = AppTheme.spacing.sm)
                                )
                            }

                            items(collections) { collection ->
                                // FIXED: Correct property access
                                val members = collectionMembers[collection.id] ?: emptyList()
                                var showDropdown by remember { mutableStateOf(false) }

                                ResponsiveCard(
                                    onClick = { onCollectionClick(collection.id) }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            // Collection Icon
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .background(
                                                        AppTheme.colors.primaryContainer,
                                                        RoundedCornerShape(AppTheme.radius.md)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Groups,
                                                    contentDescription = null,
                                                    tint = AppTheme.colors.onPrimaryContainer,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }

                                            // Collection details
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

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        AppTheme.spacing.sm
                                                    )
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = null,
                                                        tint = AppTheme.colors.onSurfaceVariant,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    ResponsiveMemberCountText(memberCount = members.size)
                                                }
                                            }
                                        }

                                        // Member Avatars Preview
                                        if (members.isNotEmpty()) {
                                            ResponsiveMemberAvatarDisplay(
                                                members = members,
                                                maxVisible = 3,
                                                avatarSize = 32,
                                                showCount = true
                                            )
                                        }

                                        // More options menu
                                        Box {
                                            IconButton(
                                                onClick = { showDropdown = true }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "More options",
                                                    tint = AppTheme.colors.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            DropdownMenu(
                                                expanded = showDropdown,
                                                onDismissRequest = { showDropdown = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = "Delete Collection",
                                                            color = AppTheme.colors.error
                                                        )
                                                    },
                                                    onClick = {
                                                        showDropdown = false
                                                        // FIXED: Correct assignment
                                                        collectionToDelete = collection
                                                        showDeleteDialog = true
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = null,
                                                            tint = AppTheme.colors.error
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Bottom spacing for FAB
                            item {
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        ResponsiveFloatingActionButton(
            onClick = { viewModel.openCollectionDialog() },
            text = "New Collection",
            icon = Icons.Default.Add,
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        // Create Collection Dialog
        if (showCollectionDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeCollectionDialog() },
                containerColor = AppTheme.colors.surface,
                shape = RoundedCornerShape(AppTheme.radius.xl),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = AppTheme.colors.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Create New Collection",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.25).sp
                            ),
                            color = AppTheme.colors.onSurface
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                    ) {
                        Text(
                            text = "Give your collection a name. You'll be automatically added as a member.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = newCollectionName,
                            onValueChange = { viewModel.updateNewCollectionName(it) },
                            label = {
                                Text(
                                    "Collection Name",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            },
                            placeholder = {
                                Text(
                                    "e.g., Weekend Trip, House Expenses",
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
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.createCollection() },
                        enabled = newCollectionName.trim().isNotEmpty() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            text = if (isLoading) "Creating..." else "Create Collection",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.closeCollectionDialog() },
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            )
        }

        // Add Member Dialog
        if (showMemberDialog && currentCollectionId != null) {
            AlertDialog(
                onDismissRequest = { viewModel.closeMemberDialog() },
                containerColor = AppTheme.colors.surface,
                shape = RoundedCornerShape(AppTheme.radius.xl),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = AppTheme.colors.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Add Member",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.25).sp
                            ),
                            color = AppTheme.colors.onSurface
                        )
                    }
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
                    ) {
                        Text(
                            text = "Add people to your collection so you can split expenses with them.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )

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
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.createAndAddNewMember() },
                        enabled = newMemberName.trim().isNotEmpty() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.primary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            text = if (isLoading) "Adding..." else "Add Member",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.closeMemberDialog() },
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && collectionToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    collectionToDelete = null
                },
                containerColor = AppTheme.colors.surface,
                shape = RoundedCornerShape(AppTheme.radius.xl),
                title = {
                    Text(
                        text = "Delete Collection",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = AppTheme.colors.onSurface
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to delete '${collectionToDelete?.name ?: "this collection"}'? This will permanently delete all expenses and data for this collection. This action cannot be undone.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = AppTheme.colors.onSurfaceVariant
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            collectionToDelete?.let { collection ->
                                viewModel.deleteCollection(collection)
                            }
                            showDeleteDialog = false
                            collectionToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.error,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            collectionToDelete = null
                        },
                        shape = RoundedCornerShape(AppTheme.radius.md)
                    ) {
                        Text(
                            text = "Cancel",
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
