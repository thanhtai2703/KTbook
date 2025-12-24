package com.kienvo.rosach.widgets.ebook

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kienvo.rosach.ui.theme.AppColors

/**
 * Banner quảng cáo Ebook Premium
 * Tái sử dụng được cho các màn hình khác
 */
@Composable
fun EbookBanner() {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(130.dp),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, AppColors.EbookBorderThin)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFBF360C), Color(0xFF5D4037)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 0f)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💎", fontSize = 32.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "Ebook Premium",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Tri thức là sức mạnh, kiến thức quý hơn vàng. Khám phá kho sách ebook đồ sộ với hơn 100 bài học quý giá.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

