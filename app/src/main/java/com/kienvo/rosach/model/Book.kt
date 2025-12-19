package com.kienvo.rosach.model

import com.kienvo.rosach.data.SampleData

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val type: String = "audiobook", // "audiobook" hoặc "ebook"
    val rating: Double = 0.0,
    val rank: Int = 0 // Rank cho top ebooks, 0 nếu không có
)

// Các hàm này giờ chỉ gọi từ SampleData
fun getBooks(): List<Book> = SampleData.allBooks

fun getPopularBooks(): List<Book> = SampleData.allBooks.filter { "popular" in it.id || it.id.toIntOrNull() in 9..16 }

fun getHealingBooks(): List<Book> = SampleData.allBooks.filter { "healing" in it.id || it.id.toIntOrNull() in 17..24 }

fun getDetectiveBooks(): List<Book> = SampleData.allBooks.filter { "detective" in it.id || it.id.toIntOrNull() in 25..32 }

fun getEbooks(): List<Book> = SampleData.allBooks.filter { it.type == "ebook" }

fun getAudiobooks(): List<Book> = SampleData.allBooks.filter { it.type == "audiobook" }
