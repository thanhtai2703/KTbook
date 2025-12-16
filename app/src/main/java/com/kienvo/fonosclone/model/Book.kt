package com.kienvo.fonosclone.model

import com.kienvo.fonosclone.data.SampleData

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String
)

// Các hàm này giờ chỉ gọi từ SampleData
fun getBooks(): List<Book> = SampleData.audioBooks

fun getPopularBooks(): List<Book> = SampleData.popularBooks

fun getHealingBooks(): List<Book> = SampleData.healingBooks

fun getDetectiveBooks(): List<Book> = SampleData.detectiveBooks
