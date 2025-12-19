package com.kienvo.rosach.widgets.ebook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kienvo.rosach.ui.theme.AppColors

/**
 * Background gradient cho màn hình Ebook
 */
@Composable
fun EbookBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AppColors.EbookBgTop,
                        AppColors.EbookBgMid,
                        AppColors.EbookBgBottom
                    ),
                    startY = 0f,
                    endY = 1800f
                )
            )
            .drawWithCache {
                val glowPurple = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFAB91).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.1f),
                    radius = size.minDimension * 1.5f
                )
                onDrawBehind { drawRect(glowPurple) }
            }
    )
}

