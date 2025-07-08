package com.puneet8goyal.splitkaro.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

object AnimationUtils {
    // Smooth entrance animations
    const val FAST_ANIMATION = 200
    const val MEDIUM_ANIMATION = 300
    const val SLOW_ANIMATION = 500

    // Spring animations for interactive elements
    val smoothSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val quickSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

// Smooth slide in from bottom animation
@Composable
fun SlideInFromBottom(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(AnimationUtils.MEDIUM_ANIMATION),
            initialOffsetY = { with(density) { 40.dp.roundToPx() } }
        ) + fadeIn(animationSpec = tween(AnimationUtils.MEDIUM_ANIMATION)),
        exit = slideOutVertically(
            animationSpec = tween(AnimationUtils.FAST_ANIMATION),
            targetOffsetY = { with(density) { 40.dp.roundToPx() } }
        ) + fadeOut(animationSpec = tween(AnimationUtils.FAST_ANIMATION))
    ) {
        content()
    }
}

// Smooth slide in from right animation
@Composable
fun SlideInFromRight(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            animationSpec = tween(AnimationUtils.MEDIUM_ANIMATION),
            initialOffsetX = { with(density) { 60.dp.roundToPx() } }
        ) + fadeIn(animationSpec = tween(AnimationUtils.MEDIUM_ANIMATION)),
        exit = slideOutHorizontally(
            animationSpec = tween(AnimationUtils.FAST_ANIMATION),
            targetOffsetX = { with(density) { 60.dp.roundToPx() } }
        ) + fadeOut(animationSpec = tween(AnimationUtils.FAST_ANIMATION))
    ) {
        content()
    }
}

// Scale animation for buttons and cards
@Composable
fun ScaleOnPress(
    modifier: Modifier = Modifier,
    scaleDown: Float = 0.95f,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = AnimationUtils.quickSpring,
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        content()
    }
}

// Staggered animation for lists
@Composable
fun StaggeredAnimation(
    visible: Boolean,
    index: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = AnimationUtils.MEDIUM_ANIMATION,
                delayMillis = index * 50 // Stagger by 50ms per item
            ),
            initialOffsetY = { it / 4 }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = AnimationUtils.MEDIUM_ANIMATION,
                delayMillis = index * 50
            )
        ),
        exit = fadeOut(animationSpec = tween(AnimationUtils.FAST_ANIMATION))
    ) {
        content()
    }
}
