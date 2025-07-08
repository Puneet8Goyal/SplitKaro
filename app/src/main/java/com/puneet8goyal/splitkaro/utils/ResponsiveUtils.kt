package com.puneet8goyal.splitkaro.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ScreenSize {
    COMPACT,    // Phones in portrait
    MEDIUM,     // Phones in landscape, small tablets
    EXPANDED    // Large tablets, desktop
}

object ResponsiveUtils {

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getScreenSize(): ScreenSize {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        return when {
            screenWidth < 600.dp -> ScreenSize.COMPACT
            screenWidth < 840.dp -> ScreenSize.MEDIUM
            else -> ScreenSize.EXPANDED
        }
    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun getMaxContentWidth(): Dp {
        return when (getScreenSize()) {
            ScreenSize.COMPACT -> LocalConfiguration.current.screenWidthDp.dp
            ScreenSize.MEDIUM -> 600.dp
            ScreenSize.EXPANDED -> 840.dp
        }
    }

    @Composable
    fun getAdaptiveColumns(): Int {
        return when (getScreenSize()) {
            ScreenSize.COMPACT -> 1
            ScreenSize.MEDIUM -> 2
            ScreenSize.EXPANDED -> 3
        }
    }

    @Composable
    fun getAdaptivePadding(): Dp {
        return when (getScreenSize()) {
            ScreenSize.COMPACT -> 16.dp
            ScreenSize.MEDIUM -> 24.dp
            ScreenSize.EXPANDED -> 32.dp
        }
    }
}

// Adaptive container that centers content on large screens
@Composable
fun AdaptiveContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val maxWidth = ResponsiveUtils.getMaxContentWidth()
    val screenSize = ResponsiveUtils.getScreenSize()

    if (screenSize == ScreenSize.EXPANDED) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .padding(horizontal = ResponsiveUtils.getAdaptivePadding())
            ) {
                content()
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = ResponsiveUtils.getAdaptivePadding())
        ) {
            content()
        }
    }
}

// Two-column layout for tablets
@Composable
fun AdaptiveTwoColumnLayout(
    primaryContent: @Composable () -> Unit,
    secondaryContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenSize = ResponsiveUtils.getScreenSize()

    when (screenSize) {
        ScreenSize.COMPACT -> {
            Column(modifier = modifier) {
                primaryContent()
                secondaryContent()
            }
        }

        ScreenSize.MEDIUM, ScreenSize.EXPANDED -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    primaryContent()
                }
                Box(modifier = Modifier.weight(1f)) {
                    secondaryContent()
                }
            }
        }
    }
}
