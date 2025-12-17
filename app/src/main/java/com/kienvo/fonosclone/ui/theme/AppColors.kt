package com.kienvo.fonosclone.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * File tập trung TẤT CẢ màu sắc của ứng dụng
 * Dễ dàng thay đổi theme và đảm bảo tính nhất quán
 */
object AppColors {

    // ============ GENERAL COLORS ============
    val DarkBg = Color(0xFF111218)
    val PaleYellow = Color(0xFFFFF176)
    val PaleYellowDark = Color(0xFFFFD54F)

    // ============ EBOOK SCREEN COLORS ============
    val EbookBgTop = Color(0xFF5D3A29)
    val EbookBgMid = Color(0xFF3E2723)
    val EbookBgBottom = Color(0xFF111218)
    val EbookPurpleBarStart = Color(0xFF422A68)
    val EbookPurpleBarEnd = Color(0xFF24135F)
    val EbookBookCardColor = Color(0xFF1E2027)
    val EbookBorderThin = Color.White.copy(alpha = 0.10f)
    val EbookSubText = Color(0xFFB6BAC3)
    val EbookInfinityPink = Color(0xFFD63D8B)

    // ============ KIDS SCREEN COLORS ============
    // Gradient Hồng cam -> Xanh ngọc -> Xanh lá
    val KidsGradientPink = Color(0xFFFF8A80)
    val KidsGradientPinkLight = Color(0xFFF48FB1)
    val KidsGradientCyan = Color(0xFF80DEEA)
    val KidsGradientGreen = Color(0xFF69F0AE)
}

