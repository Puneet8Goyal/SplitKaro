package com.puneet8goyal.splitkaro.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import kotlinx.coroutines.delay

// Premium Status Message Types
enum class StatusType {
    SUCCESS, ERROR, WARNING, INFO
}

// ENHANCED: Premium Animated Status Card
@Composable
fun PremiumStatusCard(
    message: String,
    type: StatusType = StatusType.INFO,
    onDismiss: (() -> Unit)? = null,
    autoDismiss: Boolean = false,
    autoDismissDelay: Long = 3000L,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    var isScaled by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isScaled) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Auto dismiss functionality
    LaunchedEffect(autoDismiss) {
        if (autoDismiss) {
            delay(autoDismissDelay)
            isVisible = false
            delay(300) // Wait for animation
            onDismiss?.invoke()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(300)
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(AppTheme.radius.lg),
                    ambientColor = when (type) {
                        StatusType.SUCCESS -> AppTheme.colors.success.copy(alpha = 0.2f)
                        StatusType.ERROR -> AppTheme.colors.error.copy(alpha = 0.2f)
                        StatusType.WARNING -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        StatusType.INFO -> AppTheme.colors.primary.copy(alpha = 0.2f)
                    }
                )
                .let {
                    if (onDismiss != null) {
                        it.clickable {
                            isScaled = true
                            onDismiss()
                        }
                    } else it
                },
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colors.surface
            ),
            shape = RoundedCornerShape(AppTheme.radius.lg),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(
                1.5.dp,
                when (type) {
                    StatusType.SUCCESS -> AppTheme.colors.success.copy(alpha = 0.3f)
                    StatusType.ERROR -> AppTheme.colors.error.copy(alpha = 0.3f)
                    StatusType.WARNING -> Color(0xFFFF9800).copy(alpha = 0.3f)
                    StatusType.INFO -> AppTheme.colors.primary.copy(alpha = 0.3f)
                }
            )
        ) {
            Box {
                // Gradient background overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = when (type) {
                                    StatusType.SUCCESS -> listOf(
                                        AppTheme.colors.success,
                                        AppTheme.colors.success.copy(alpha = 0.7f)
                                    )
                                    StatusType.ERROR -> listOf(
                                        AppTheme.colors.error,
                                        AppTheme.colors.error.copy(alpha = 0.7f)
                                    )
                                    StatusType.WARNING -> listOf(
                                        Color(0xFFFF9800),
                                        Color(0xFFFF9800).copy(alpha = 0.7f)
                                    )
                                    StatusType.INFO -> listOf(
                                        AppTheme.colors.primary,
                                        AppTheme.colors.primary.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        )
                )

                Row(
                    modifier = Modifier.padding(AppTheme.spacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
                ) {
                    // Premium icon with background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = when (type) {
                                        StatusType.SUCCESS -> listOf(
                                            AppTheme.colors.success.copy(alpha = 0.15f),
                                            AppTheme.colors.success.copy(alpha = 0.05f)
                                        )
                                        StatusType.ERROR -> listOf(
                                            AppTheme.colors.error.copy(alpha = 0.15f),
                                            AppTheme.colors.error.copy(alpha = 0.05f)
                                        )
                                        StatusType.WARNING -> listOf(
                                            Color(0xFFFF9800).copy(alpha = 0.15f),
                                            Color(0xFFFF9800).copy(alpha = 0.05f)
                                        )
                                        StatusType.INFO -> listOf(
                                            AppTheme.colors.primary.copy(alpha = 0.15f),
                                            AppTheme.colors.primary.copy(alpha = 0.05f)
                                        )
                                    }
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (type) {
                                StatusType.SUCCESS -> Icons.Filled.CheckCircle
                                StatusType.ERROR -> Icons.Outlined.Error
                                StatusType.WARNING -> Icons.Outlined.Warning
                                StatusType.INFO -> Icons.Outlined.Info
                            },
                            contentDescription = null,
                            tint = when (type) {
                                StatusType.SUCCESS -> AppTheme.colors.success
                                StatusType.ERROR -> AppTheme.colors.error
                                StatusType.WARNING -> Color(0xFFFF9800)
                                StatusType.INFO -> AppTheme.colors.primary
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.1.sp,
                            lineHeight = 22.sp
                        ),
                        color = AppTheme.colors.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    if (onDismiss != null) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = AppTheme.colors.onSurface.copy(alpha = 0.08f)
                        ) {
                            IconButton(
                                onClick = {
                                    isVisible = false
                                    onDismiss()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = AppTheme.colors.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Legacy ModernStatusCard (updated to use PremiumStatusCard)
@Composable
fun ModernStatusCard(
    message: String,
    isSuccess: Boolean,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    PremiumStatusCard(
        message = message,
        type = if (isSuccess) StatusType.SUCCESS else StatusType.ERROR,
        onDismiss = onDismiss,
        autoDismiss = true,
        modifier = modifier
    )
}

// Legacy ModernStatusMessage (updated to use PremiumStatusCard)
@Composable
fun ModernStatusMessage(
    message: String,
    isSuccess: Boolean
) {
    PremiumStatusCard(
        message = message,
        type = if (isSuccess) StatusType.SUCCESS else StatusType.ERROR,
        autoDismiss = true,
        autoDismissDelay = 2500L
    )
}

@Composable
fun UserOnboardingDialog(
    onUserInfoSubmitted: (String) -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { },
        containerColor = AppTheme.colors.surface,
        shape = RoundedCornerShape(AppTheme.radius.xl),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
            ) {
                Text(
                    text = "👋",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 48.sp
                    )
                )
                Text(
                    text = "Welcome to SplitKaro!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.25).sp
                    ),
                    color = AppTheme.colors.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
            ) {
                Text(
                    text = "To get started, please tell us your name. This will help us track your expenses and show you who owes what.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 24.sp
                    ),
                    color = AppTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = {
                        Text(
                            "Your Name",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    },
                    placeholder = {
                        Text(
                            "Enter your name",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSubmitting,
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
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (userName.trim().isNotEmpty() && !isSubmitting) {
                        isSubmitting = true
                        onUserInfoSubmitted(userName.trim())
                    }
                },
                enabled = userName.trim().isNotEmpty() && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primary,
                    contentColor = Color.White,
                    disabledContainerColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.12f),
                    disabledContentColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.38f)
                ),
                shape = RoundedCornerShape(AppTheme.radius.lg),
                modifier = Modifier.height(56.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
                    Text("Setting up...")
                } else {
                    Text(
                        "Get Started",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.1.sp
                        )
                    )
                }
            }
        }
    )
}

@Composable
fun ModernLoadingState(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.huge),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xl)
        ) {
            CircularProgressIndicator(
                color = AppTheme.colors.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = AppTheme.colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MemberAvatar(
    member: Member,
    size: Int = 40,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
    borderColor: Color = AppTheme.colors.primary
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .let {
                if (showBorder) {
                    it.border(2.dp, borderColor, CircleShape)
                } else it
            }
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        AppUtils.getAvatarColor(member.name),
                        AppUtils.getAvatarColor(member.name).copy(alpha = 0.8f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = AppUtils.getInitials(member.name),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.25).sp
            ),
            fontSize = (size * 0.4).sp
        )
    }
}

@Composable
fun ModernSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClear: () -> Unit,
    placeholder: String = "Search expenses...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.colors.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = AppTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = onSearchClear) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = "Clear",
                        tint = AppTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppTheme.colors.primary,
            unfocusedBorderColor = AppTheme.colors.border,
            focusedTextColor = AppTheme.colors.onSurface,
            unfocusedTextColor = AppTheme.colors.onSurface,
            cursorColor = AppTheme.colors.primary
        ),
        shape = RoundedCornerShape(AppTheme.radius.md),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
fun ModernSectionCard(
    title: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface
        ),
        shape = RoundedCornerShape(AppTheme.radius.xl),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.lg)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                AppTheme.colors.primaryContainer,
                                RoundedCornerShape(AppTheme.radius.sm)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = AppTheme.colors.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.25).sp
                        ),
                        color = AppTheme.colors.onSurface
                    )

                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = AppTheme.colors.onSurfaceVariant
                        )
                    }
                }
            }

            content()
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (value.isNotEmpty()) AppTheme.colors.primary else AppTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = keyboardOptions,
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
        )
    )
}

@Composable
fun ModernMemberCard(
    member: Member,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectionType: String
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                AppTheme.colors.primaryContainer
            else
                AppTheme.colors.surfaceVariant
        ),
        shape = RoundedCornerShape(AppTheme.radius.lg),
        border = if (isSelected)
            BorderStroke(2.dp, AppTheme.colors.primary)
        else
            BorderStroke(1.dp, AppTheme.colors.border),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
        ) {
            Box {
                MemberAvatar(
                    member = member,
                    size = 32
                )

                if (isSelected && selectionType == "radio") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp)
                            .background(AppTheme.colors.success, CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            Text(
                text = member.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    letterSpacing = (-0.25).sp
                ),
                color = if (isSelected) AppTheme.colors.onPrimaryContainer else AppTheme.colors.onSurface,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernMemberRow(
    member: Member,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                AppTheme.colors.primaryContainer.copy(alpha = 0.3f)
            else
                AppTheme.colors.surfaceContainer
        ),
        shape = RoundedCornerShape(AppTheme.radius.md),
        border = if (isSelected)
            BorderStroke(1.dp, AppTheme.colors.primary.copy(alpha = 0.5f))
        else null
    ) {
        Row(
            modifier = Modifier.padding(AppTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            MemberAvatar(
                member = member,
                size = 32
            )

            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.15.sp
                ),
                color = AppTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )

            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                colors = CheckboxDefaults.colors(
                    checkedColor = AppTheme.colors.primary,
                    uncheckedColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.6f),
                    checkmarkColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ModernPreviewCard(
    totalAmount: Double,
    perPersonAmount: Double,
    memberCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.successContainer
        ),
        shape = RoundedCornerShape(AppTheme.radius.lg),
        border = BorderStroke(1.dp, AppTheme.colors.success.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
        ) {
            Text(
                text = "Split Preview",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.25).sp
                ),
                color = AppTheme.colors.onSuccessContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total amount:",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = AppTheme.colors.onSuccessContainer.copy(alpha = 0.8f)
                )

                Text(
                    text = AppUtils.formatCurrency(totalAmount),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.25).sp
                    ),
                    color = AppTheme.colors.onSuccessContainer
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Split among $memberCount:",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = AppTheme.colors.onSuccessContainer.copy(alpha = 0.8f)
                )

                Text(
                    text = AppUtils.formatCurrency(perPersonAmount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.25).sp
                    ),
                    color = AppTheme.colors.success
                )
            }
        }
    }
}

@Composable
fun ModernErrorCard(
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.errorContainer
        ),
        shape = RoundedCornerShape(AppTheme.radius.lg),
        border = BorderStroke(1.dp, AppTheme.colors.error.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.25).sp
                ),
                color = AppTheme.colors.onErrorContainer
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = AppTheme.colors.onErrorContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ModernBalanceBadge(
    amount: Double,
    isPositive: Boolean
) {
    Surface(
        color = if (isPositive) AppTheme.colors.success.copy(alpha = 0.15f) else AppTheme.colors.error.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown,
                contentDescription = null,
                tint = if (isPositive) AppTheme.colors.success else AppTheme.colors.error,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = AppUtils.formatCurrency(kotlin.math.abs(amount)),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.1.sp
                ),
                color = if (isPositive) AppTheme.colors.success else AppTheme.colors.error
            )
        }
    }
}

@Composable
fun ModernDebtRow(
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
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.1.sp
            ),
            color = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = AppUtils.formatCurrency(amount),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.1).sp
            ),
            color = if (isPositive) AppTheme.colors.success else AppTheme.colors.error
        )
    }
}
