package com.kienvo.rosach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.model.BookCategory
import com.kienvo.rosach.repository.BookRepository
import com.kienvo.rosach.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    private val bookRepository = BookRepository()
    private val categoryRepository = CategoryRepository()

    // State cho tất cả sách
    private val _allBooks = MutableStateFlow<List<Book>>(emptyList())
    val allBooks: StateFlow<List<Book>> = _allBooks.asStateFlow()

    // State cho categories
    private val _categories = MutableStateFlow<List<BookCategory>>(emptyList())
    val categories: StateFlow<List<BookCategory>> = _categories.asStateFlow()

    // State cho featured books (carousel)
    private val _featuredBooks = MutableStateFlow<List<Book>>(emptyList())
    val featuredBooks: StateFlow<List<Book>> = _featuredBooks.asStateFlow()

    // [MỚI] State cho các sections đã shuffle (chỉ tính 1 lần)
    private val _recommendedBooks = MutableStateFlow<List<Book>>(emptyList())
    val recommendedBooks: StateFlow<List<Book>> = _recommendedBooks.asStateFlow()

    private val _topBooks = MutableStateFlow<List<Book>>(emptyList())
    val topBooks: StateFlow<List<Book>> = _topBooks.asStateFlow()

    private val _favoriteBooks = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooks: StateFlow<List<Book>> = _favoriteBooks.asStateFlow()

    private val _newBooks = MutableStateFlow<List<Book>>(emptyList())
    val newBooks: StateFlow<List<Book>> = _newBooks.asStateFlow()

    private val _kidsBooks = MutableStateFlow<List<Book>>(emptyList())
    val kidsBooks: StateFlow<List<Book>> = _kidsBooks.asStateFlow()

    private val _trendingBooks = MutableStateFlow<List<Book>>(emptyList())
    val trendingBooks: StateFlow<List<Book>> = _trendingBooks.asStateFlow()

    // State cho loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State cho error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Cache sách theo category
    private val _booksByCategory = MutableStateFlow<Map<String, List<Book>>>(emptyMap())
    val booksByCategory: StateFlow<Map<String, List<Book>>> = _booksByCategory.asStateFlow()

    /**
     * Load tất cả sách
     */
    fun loadAllBooks(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val books = bookRepository.getAllBooks(forceRefresh)
                _allBooks.value = books

                // Cũng load featured books
                val featured = books.filter { it.rank in 1..10 || books.indexOf(it) < 10 }
                _featuredBooks.value = featured.take(10)

                // [MỚI] Tính toán các sections CHỈ 1 LẦN khi load data
                if (_recommendedBooks.value.isEmpty()) {
                    _recommendedBooks.value = books.filter { it.rating >= 4.0 }.shuffled().take(10)
                }

                if (_topBooks.value.isEmpty()) {
                    _topBooks.value = books.sortedByDescending { it.rating }.take(10)
                }

                if (_favoriteBooks.value.isEmpty()) {
                    _favoriteBooks.value = books.filter { it.rating >= 4.5 }.take(10)
                }

                if (_newBooks.value.isEmpty()) {
                    _newBooks.value = books.shuffled().take(10)
                }

                if (_kidsBooks.value.isEmpty()) {
                    _kidsBooks.value = books.filter { it.id.startsWith("kid_") }
                }

                if (_trendingBooks.value.isEmpty()) {
                    _trendingBooks.value = books.filter { it.rating >= 4.2 }.shuffled().take(10)
                }

            } catch (e: Exception) {
                _error.value = "Lỗi khi tải sách: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load categories
     */
    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = categoryRepository.getAllCategories()
                _categories.value = categories
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải danh mục: ${e.message}"
            }
        }
    }

    /**
     * Load sách theo category
     */
    fun loadBooksByCategory(category: String) {
        viewModelScope.launch {
            try {
                // Check cache trước
                if (_booksByCategory.value.containsKey(category)) {
                    return@launch
                }

                val books = bookRepository.getBooksByCategory(category)
                val updatedMap = _booksByCategory.value.toMutableMap()
                updatedMap[category] = books
                _booksByCategory.value = updatedMap

            } catch (e: Exception) {
                _error.value = "Lỗi khi tải sách theo danh mục: ${e.message}"
            }
        }
    }

    /**
     * Load tất cả sách cho tất cả categories (dùng cho HomeScreen)
     */
    fun loadAllCategoriesWithBooks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Load categories trước
                val categories = categoryRepository.getAllCategories()
                _categories.value = categories

                // Load sách cho từng category
                categories.forEach { category ->
                    loadBooksByCategory(category.slug)
                }

            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get books theo category slug
     */
    fun getBooksForCategory(categorySlug: String): List<Book> {
        return _booksByCategory.value[categorySlug] ?: emptyList()
    }

    /**
     * Search sách
     */
    fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val results = bookRepository.searchBooks(query)
                _allBooks.value = results
            } catch (e: Exception) {
                _error.value = "Lỗi tìm kiếm: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get book by ID
     */
    suspend fun getBookById(bookId: String): Book? {
        return bookRepository.getBookById(bookId)
    }

    /**
     * Clear error
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Refresh data
     */
    fun refresh() {
        bookRepository.clearCache()
        loadAllBooks(forceRefresh = true)
        loadCategories()
    }
}
