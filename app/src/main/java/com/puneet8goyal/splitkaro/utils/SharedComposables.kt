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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.outlined.PersonAdd
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.puneet8goyal.splitkaro.data.Member
import com.puneet8goyal.splitkaro.ui.theme.AppTheme
import kotlinx.coroutines.delay

// ENUMS AND TYPES
enum class StatusType {
    SUCCESS, ERROR, WARNING, INFO
}

// RESPONSIVE MEMBER DISPLAY SYSTEM
@Composable
fun ResponsiveMemberAvatarDisplay(
    members: List<Member>,
    maxVisible: Int = 3,
    modifier: Modifier = Modifier,
    avatarSize: Int = 32,
    showCount: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive sizing based on screen width
    val adaptiveAvatarSize = when {
        screenWidth < 360.dp -> (avatarSize * 0.8f).toInt()
        screenWidth < 480.dp -> (avatarSize * 0.9f).toInt()
        else -> avatarSize
    }

    val adaptiveMaxVisible = when {
        screenWidth < 360.dp -> 2
        screenWidth < 480.dp -> 3
        else -> maxVisible
    }

    Row(
        modifier = modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy((-6).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show visible avatars
        members.take(adaptiveMaxVisible).forEach { member ->
            MemberAvatar(
                member = member,
                size = adaptiveAvatarSize,
                showBorder = true,
                borderColor = AppTheme.colors.surface
            )
        }

        // Show count if there are more members
        if (members.size > adaptiveMaxVisible) {
            Spacer(modifier = Modifier.width(2.dp))
            Box(
                modifier = Modifier
                    .size(adaptiveAvatarSize.dp)
                    .background(
                        AppTheme.colors.surfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+${members.size - adaptiveMaxVisible}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = when {
                            screenWidth < 360.dp -> 9.sp
                            screenWidth < 480.dp -> 10.sp
                            else -> 11.sp
                        }
                    ),
                    color = AppTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ResponsiveMemberCountText(
    memberCount: Int,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val textStyle = when {
        screenWidth < 360.dp -> MaterialTheme.typography.bodySmall
        screenWidth < 480.dp -> MaterialTheme.typography.bodyMedium
        else -> MaterialTheme.typography.bodyMedium
    }

    Text(
        text = "$memberCount ${if (memberCount == 1) "member" else "members"}",
        style = textStyle.copy(
            fontWeight = FontWeight.Medium
        ),
        color = AppTheme.colors.onSurfaceVariant,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResponsiveMemberSelector(
    members: List<Member>,
    selectedMemberIds: List<Long>,
    onMemberClick: (Member) -> Unit,
    selectionType: String = "checkbox", // "checkbox" or "radio"
    label: String = "Select Members",
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Determine layout based on screen size
    val useGrid = screenWidth < 600.dp
    val maxItemsPerRow = when {
        screenWidth < 360.dp -> 2
        screenWidth < 480.dp -> 3
        screenWidth < 600.dp -> 4
        else -> 5
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.25).sp
            ),
            color = AppTheme.colors.onSurface
        )

        if (useGrid) {
            // Use FlowRow for smaller screens (auto-wrapping grid)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sm),
                maxItemsInEachRow = maxItemsPerRow
            ) {
                members.forEach { member ->
                    val isSelected = when (selectionType) {
                        "radio" -> selectedMemberIds.firstOrNull() == member.id
                        else -> member.id in selectedMemberIds
                    }

                    ResponsiveMemberCard(
                        member = member,
                        isSelected = isSelected,
                        onClick = { onMemberClick(member) },
                        selectionType = selectionType,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }
        } else {
            // Use LazyRow for larger screens
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
                contentPadding = PaddingValues(horizontal = AppTheme.spacing.xs)
            ) {
                items(members) { member ->
                    val isSelected = when (selectionType) {
                        "radio" -> selectedMemberIds.firstOrNull() == member.id
                        else -> member.id in selectedMemberIds
                    }

                    ResponsiveMemberCard(
                        member = member,
                        isSelected = isSelected,
                        onClick = { onMemberClick(member) },
                        selectionType = selectionType
                    )
                }
            }
        }
    }
}

@Composable
fun ResponsiveMemberCard(
    member: Member,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectionType: String = "checkbox",
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // Adaptive sizing based on screen width
    val cardWidth = when {
        screenWidth < 360.dp -> 80.dp
        screenWidth < 480.dp -> 90.dp
        screenWidth < 600.dp -> 100.dp
        else -> 110.dp
    }

    val cardHeight = when {
        screenWidth < 360.dp -> 100.dp
        screenWidth < 480.dp -> 110.dp
        screenWidth < 600.dp -> 120.dp
        else -> 130.dp
    }

    val avatarSize = when {
        screenWidth < 360.dp -> 32
        screenWidth < 480.dp -> 36
        screenWidth < 600.dp -> 40
        else -> 44
    }

    val fontSize = when {
        screenWidth < 360.dp -> 11.sp
        screenWidth < 480.dp -> 12.sp
        else -> 13.sp
    }

    Card(
        modifier = modifier
            .widthIn(min = cardWidth, max = cardWidth)
            .height(cardHeight)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AppTheme.colors.primaryContainer else AppTheme.colors.surface
        ),
        shape = RoundedCornerShape(AppTheme.radius.md),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.border
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
        ) {
            // Member Avatar with selection indicator
            Box(
                modifier = Modifier.size(avatarSize.dp),
                contentAlignment = Alignment.Center
            ) {
                MemberAvatar(
                    member = member,
                    size = avatarSize,
                    showBorder = isSelected,
                    borderColor = AppTheme.colors.primary
                )

                // Selection indicator overlay
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(AppTheme.colors.primary, CircleShape)
                            .align(Alignment.BottomEnd),
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

            // Member name with proper truncation
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = fontSize,
                    letterSpacing = (-0.1).sp
                ),
                color = if (isSelected) AppTheme.colors.onPrimaryContainer else AppTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            // Selection control (checkbox/radio)
            when (selectionType) {
                "radio" -> RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AppTheme.colors.primary,
                        unselectedColor = AppTheme.colors.onSurfaceVariant
                    ),
                    modifier = Modifier.size(16.dp)
                )

                "checkbox" -> Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AppTheme.colors.primary,
                        uncheckedColor = AppTheme.colors.onSurfaceVariant
                    ),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// RESPONSIVE COMPONENTS SYSTEM
@Composable
fun ResponsiveRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}

@Composable
fun ResponsiveCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val horizontalPadding = when {
        screenWidth < 360.dp -> AppTheme.spacing.md
        screenWidth < 480.dp -> AppTheme.spacing.lg
        screenWidth < 600.dp -> AppTheme.spacing.xl
        else -> AppTheme.spacing.xxl
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it },
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(AppTheme.radius.xl),
        border = BorderStroke(1.dp, AppTheme.colors.border)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = horizontalPadding,
                vertical = AppTheme.spacing.xl
            ),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.md),
            content = content
        )
    }
}

@Composable
fun ResponsiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val buttonHeight = when {
        screenWidth < 360.dp -> 48.dp
        screenWidth < 480.dp -> 52.dp
        else -> 56.dp
    }

    val fontSize = when {
        screenWidth < 360.dp -> 14.sp
        screenWidth < 480.dp -> 15.sp
        else -> 16.sp
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(buttonHeight)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) AppTheme.colors.primary else AppTheme.colors.secondary,
            contentColor = Color.White,
            disabledContainerColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.12f),
            disabledContentColor = AppTheme.colors.onSurfaceVariant.copy(alpha = 0.38f)
        ),
        shape = RoundedCornerShape(AppTheme.radius.lg),
        contentPadding = PaddingValues(
            horizontal = AppTheme.spacing.xl,
            vertical = AppTheme.spacing.md
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = fontSize
            )
        )
    }
}

@Composable
fun ResponsiveFloatingActionButton(
    onClick: () -> Unit,
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val fabPadding = when {
        screenWidth < 360.dp -> AppTheme.spacing.lg
        screenWidth < 480.dp -> AppTheme.spacing.xl
        else -> AppTheme.spacing.xxl
    }

    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier.padding(fabPadding),
        containerColor = AppTheme.colors.primary,
        contentColor = Color.White,
        shape = RoundedCornerShape(AppTheme.radius.lg)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.sm))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@Composable
fun ModernIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val buttonSize = when {
        screenWidth < 360.dp -> 40.dp
        screenWidth < 480.dp -> 44.dp
        else -> 48.dp
    }

    IconButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.size((buttonSize.value * 0.5f).dp)
        )
    }
}

@Composable
fun ModernBalanceRow(
    label: String,
    amount: Double,
    isPositive: Boolean
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val textSize = when {
        screenWidth < 360.dp -> MaterialTheme.typography.bodySmall
        screenWidth < 480.dp -> MaterialTheme.typography.bodyMedium
        else -> MaterialTheme.typography.bodyLarge
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = textSize.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.1.sp
            ),
            color = AppTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = AppUtils.formatCurrency(amount),
            style = textSize.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.1).sp
            ),
            color = if (isPositive) AppTheme.colors.success else AppTheme.colors.error
        )
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
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

// LEGACY MEMBER CARD (for backward compatibility)
@Composable
fun ModernMemberCard(
    member: Member,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectionType: String = "checkbox"
) {
    ResponsiveMemberCard(
        member = member,
        isSelected = isSelected,
        onClick = onClick,
        selectionType = selectionType
    )
}

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

    LaunchedEffect(autoDismiss) {
        if (autoDismiss) {
            delay(autoDismissDelay)
            isVisible = false
            delay(300)
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

// Legacy support components
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
