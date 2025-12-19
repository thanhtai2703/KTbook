package com.kienvo.rosach.model

data class BookCategory(
    val id: String,
    val title: String,      // Tên danh mục
    val books: List<Book>   // Danh sách các cuốn sách trong danh mục đó
)

// Hàm này giả lập việc lấy toàn bộ trang chủ từ Server về
fun getHomeScreenData(): List<BookCategory> {
    val allBooks = getBooks() // Lấy kho sách chung

    return listOf(
        BookCategory("1", "Top Thịnh Hành", getPopularBooks()),
        BookCategory("2", "Mới Ra Mắt", allBooks.shuffled().take(5)), // Lấy ngẫu nhiên 5 cuốn
        BookCategory("3", "Sách Chữa Lành", getHealingBooks()),
        BookCategory("4", "Tiểu Thuyết Trinh Thám", getDetectiveBooks()),
        BookCategory("5", "Sách Kinh Tế & Làm Giàu", allBooks.shuffled().take(6)),
        BookCategory("6", "Dành Cho Thiếu Nhi", allBooks.shuffled().take(4)),
        BookCategory("7", "Tự Truyện & Hồi Ký", allBooks.shuffled().take(5))
    )
}

