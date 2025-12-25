package com.kienvo.rosach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.repository.BookRepository
import com.kienvo.rosach.service.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class RecommendationViewModel : ViewModel() {
    private val geminiService = GeminiService()
    private val bookRepository = BookRepository()

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Xin chào! Tôi là RoSach Librarian. Bạn đang muốn tìm cuốn sách như thế nào? (VD: Sách cho trẻ em, sách trinh thám kịch tính...)", false)
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _recommendedBook = MutableStateFlow<Book?>(null)
    val recommendedBook: StateFlow<Book?> = _recommendedBook.asStateFlow()

    /**
     * Gửi câu hỏi tới AI
     */
    fun askAI(query: String) {
        if (query.isBlank()) return

        // 1. Thêm tin nhắn của user vào UI
        _messages.value = _messages.value + ChatMessage(query, true)
        _isLoading.value = true
        _recommendedBook.value = null // Clear gợi ý cũ

        viewModelScope.launch {
            try {
                // 2. Lấy danh sách sách từ Firestore để làm "kiến thức" cho AI
                val allBooks = bookRepository.getAllBooks()
                val catalog = allBooks.joinToString("\n") { book ->
                    "ID: ${book.id}, Title: ${book.title}, Author: ${book.author}, Description: ${book.description}"
                }

                // 3. Gọi Gemini
                val aiResponse = geminiService.getRecommendation(query, catalog)

                if (aiResponse != null) {
                    // 4. Parse ID sách từ response (tìm chuỗi trong ngoặc [])
                    val bookId = parseBookId(aiResponse)
                    
                    if (bookId != null) {
                        val book = bookRepository.getBookById(bookId)
                        _recommendedBook.value = book
                    }

                    // 5. Thêm phản hồi của AI vào chat
                    _messages.value = _messages.value + ChatMessage(aiResponse, false)
                } else {
                    _messages.value = _messages.value + ChatMessage("Xin lỗi, tôi gặp chút trục trặc khi kết nối. Bạn thử lại nhé!", false)
                }
            } catch (e: Exception) {
                android.util.Log.e("RecommendationVM", "Ask AI failed: ${e.message}", e)
                _messages.value = _messages.value + ChatMessage("Lỗi: ${e.message}", false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseBookId(text: String): String? {
        val regex = Regex("""\[(.*?)]""")
        val match = regex.find(text)
        return match?.groupValues?.get(1)
    }

    fun clearChat() {
        _messages.value = listOf(ChatMessage("Chào mừng quay lại! Tôi có thể giúp gì cho bạn hôm nay?", false))
        _recommendedBook.value = null
    }
}
