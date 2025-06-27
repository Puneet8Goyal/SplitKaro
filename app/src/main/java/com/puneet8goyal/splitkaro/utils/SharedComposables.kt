package com.puneet8goyal.splitkaro.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.puneet8goyal.splitkaro.data.Member

@Composable
fun MemberAvatar(
    member: Member,
    size: Int = 40
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(
                color = AppUtils.getAvatarColor(member.name),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = AppUtils.getInitials(member.name),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodySmall
        )
    }
}