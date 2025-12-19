package com.kienvo.rosach.model

data class BookPart(
    val id: String = "",
    val bookId: String = "",
    val partNumber: Int = 0,
    val title: String = "",
    val audioUrl: String = "",
    val duration: String = "",
    val durationSeconds: Int = 0,
    val fileSize: String = "",
    val isPremium: Boolean = false,
    val isFree: Boolean = true,
    val order: Int = 0
)

