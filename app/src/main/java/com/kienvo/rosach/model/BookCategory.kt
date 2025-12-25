package com.kienvo.rosach.model

data class BookCategory(
    val id: String = "",
    val name: String = "",           // Đổi từ title → name
    val slug: String = "",
    val type: String = "audiobook",
    val description: String = "",
    val imageUrl: String = "",
    val color: String = "#6D4C41",   // Hex color string
    val order: Int = 0,
    val isActive: Boolean = true
)

// Legacy support - để tương thích với code cũ
data class BookCategoryWithBooks(
    val id: String,
    val title: String,
    val books: List<Book>
)

// Hàm này giả lập việc lấy toàn bộ trang chủ từ Server về (GIỮ LẠI ĐỂ FALLBACK)
fun getHomeScreenData(): List<BookCategoryWithBooks> {
    val allBooks = getBooks() // Lấy kho sách chung

    return listOf(
        BookCategoryWithBooks("1", "Top Thịnh Hành", getPopularBooks()),
        BookCategoryWithBooks("2", "Mới Ra Mắt", allBooks.shuffled().take(5)),
        BookCategoryWithBooks("3", "Sách Chữa Lành", getHealingBooks()),
        BookCategoryWithBooks("4", "Tiểu Thuyết Trinh Thám", getDetectiveBooks()),
        BookCategoryWithBooks("5", "Sách Kinh Tế & Làm Giàu", allBooks.shuffled().take(6)),
        BookCategoryWithBooks("6", "Dành Cho Thiếu Nhi", allBooks.shuffled().take(4)),
        BookCategoryWithBooks("7", "Tự Truyện & Hồi Ký", allBooks.shuffled().take(5))
    )
}
