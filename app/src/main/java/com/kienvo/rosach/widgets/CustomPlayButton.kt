package com.kienvo.rosach.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kienvo.rosach.ui.theme.Orange
import com.kienvo.rosach.ui.theme.RedDark

@Composable
fun CustomPlayButton(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    onClick: () -> Unit = {}
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Orange, RedDark)
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(brush = gradientBrush)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}