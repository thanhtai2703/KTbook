package com.kienvo.rosach.viewmodel

import androidx.lifecycle.ViewModel
import com.kienvo.rosach.data.SampleData
import com.kienvo.rosach.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SortOption {
    NAME_ASC,       // Tên A-Z
    NAME_DESC,      // Tên Z-A
    AUTHOR_ASC,     // Tác giả A-Z
    AUTHOR_DESC,    // Tác giả Z-A
    RECENTLY_ADDED  // Thêm gần đây
}

class LibraryViewModel : ViewModel() {

    // State cho các tab trong Library
    private val _currentlyListening = MutableStateFlow<List<Book>>(SampleData.popularBooks.take(3))
    val currentlyListening: StateFlow<List<Book>> = _currentlyListening.asStateFlow()

    private val _favorites = MutableStateFlow<List<Book>>(SampleData.healingBooks.take(8))
    val favorites: StateFlow<List<Book>> = _favorites.asStateFlow()

    private val _history = MutableStateFlow<List<Book>>((SampleData.popularBooks + SampleData.healingBooks).take(12))
    val history: StateFlow<List<Book>> = _history.asStateFlow()

    private val _downloads = MutableStateFlow<List<Book>>(SampleData.detectiveBooks.take(6))
    val downloads: StateFlow<List<Book>> = _downloads.asStateFlow()

    // State cho progress của sách đang nghe (bookId -> progress)
    private val _listeningProgress = MutableStateFlow<Map<String, Float>>(
        mapOf(
            SampleData.popularBooks[0].id to 0.45f,
            SampleData.popularBooks[1].id to 0.65f,
            SampleData.popularBooks[2].id to 0.23f
        )
    )
    val listeningProgress: StateFlow<Map<String, Float>> = _listeningProgress.asStateFlow()

    // State cho tùy chọn sắp xếp yêu thích
    private val _favoriteSortOption = MutableStateFlow(SortOption.RECENTLY_ADDED)
    val favoriteSortOption: StateFlow<SortOption> = _favoriteSortOption.asStateFlow()

    // State cho dialog quản lý tải xuống
    private val _showDownloadManageDialog = MutableStateFlow(false)
    val showDownloadManageDialog: StateFlow<Boolean> = _showDownloadManageDialog.asStateFlow()

    /**
     * Xóa sách khỏi danh sách đang nghe
     */
    fun removeFromCurrentlyListening(book: Book) {
        _currentlyListening.value = _currentlyListening.value.filter { it.id != book.id }
        // Xóa progress
        val updatedProgress = _listeningProgress.value.toMutableMap()
        updatedProgress.remove(book.id)
        _listeningProgress.value = updatedProgress
    }

    /**
     * Thêm sách vào danh sách đang nghe
     */
    fun addToCurrentlyListening(book: Book, progress: Float = 0f) {
        if (_currentlyListening.value.none { it.id == book.id }) {
            _currentlyListening.value = listOf(book) + _currentlyListening.value
            // Thêm progress
            val updatedProgress = _listeningProgress.value.toMutableMap()
            updatedProgress[book.id] = progress
            _listeningProgress.value = updatedProgress
        }
    }

    /**
     * Cập nhật progress của sách
     */
    fun updateListeningProgress(bookId: String, progress: Float) {
        val updatedProgress = _listeningProgress.value.toMutableMap()
        updatedProgress[bookId] = progress
        _listeningProgress.value = updatedProgress
    }

    /**
     * Lấy progress của sách
     */
    fun getProgress(bookId: String): Float {
        return _listeningProgress.value[bookId] ?: 0f
    }

    /**
     * Xóa sách khỏi yêu thích
     */
    fun removeFromFavorites(book: Book) {
        _favorites.value = _favorites.value.filter { it.id != book.id }
    }

    /**
     * Thêm sách vào yêu thích
     */
    fun addToFavorites(book: Book) {
        if (_favorites.value.none { it.id == book.id }) {
            _favorites.value = listOf(book) + _favorites.value
        }
    }

    /**
     * Toggle favorite
     */
    fun toggleFavorite(book: Book) {
        if (_favorites.value.any { it.id == book.id }) {
            removeFromFavorites(book)
        } else {
            addToFavorites(book)
        }
    }

    /**
     * Kiểm tra sách có trong yêu thích không
     */
    fun isFavorite(bookId: String): Boolean {
        return _favorites.value.any { it.id == bookId }
    }

    /**
     * Xóa sách khỏi lịch sử
     */
    fun removeFromHistory(book: Book) {
        _history.value = _history.value.filter { it.id != book.id }
    }

    /**
     * Thêm sách vào lịch sử
     */
    fun addToHistory(book: Book) {
        // Xóa nếu đã tồn tại, sau đó thêm lên đầu
        val filtered = _history.value.filter { it.id != book.id }
        _history.value = listOf(book) + filtered
    }

    /**
     * Xóa toàn bộ lịch sử
     */
    fun clearAllHistory() {
        _history.value = emptyList()
    }

    /**
     * Xóa sách khỏi tải xuống
     */
    fun removeFromDownloads(book: Book) {
        _downloads.value = _downloads.value.filter { it.id != book.id }
    }

    /**
     * Thêm sách vào tải xuống
     */
    fun addToDownloads(book: Book) {
        if (_downloads.value.none { it.id == book.id }) {
            _downloads.value = listOf(book) + _downloads.value
        }
    }

    /**
     * Kiểm tra sách đã tải xuống chưa
     */
    fun isDownloaded(bookId: String): Boolean {
        return _downloads.value.any { it.id == bookId }
    }

    /**
     * Lấy tổng dung lượng tải xuống (giả lập)
     */
    fun getTotalDownloadSize(): String {
        val totalMB = _downloads.value.size * 42 // Mỗi sách ~42MB
        return "$totalMB MB"
    }

    /**
     * Sắp xếp danh sách yêu thích
     */
    fun sortFavorites(sortOption: SortOption) {
        _favoriteSortOption.value = sortOption
        _favorites.value = when (sortOption) {
            SortOption.NAME_ASC -> _favorites.value.sortedBy { it.title.lowercase() }
            SortOption.NAME_DESC -> _favorites.value.sortedByDescending { it.title.lowercase() }
            SortOption.AUTHOR_ASC -> _favorites.value.sortedBy { it.author.lowercase() }
            SortOption.AUTHOR_DESC -> _favorites.value.sortedByDescending { it.author.lowercase() }
            SortOption.RECENTLY_ADDED -> _favorites.value // Giữ nguyên thứ tự hiện tại
        }
    }

    /**
     * Hiển thị dialog quản lý tải xuống
     */
    fun showDownloadManageDialog() {
        _showDownloadManageDialog.value = true
    }

    /**
     * Đóng dialog quản lý tải xuống
     */
    fun hideDownloadManageDialog() {
        _showDownloadManageDialog.value = false
    }

    /**
     * Xóa tất cả tải xuống
     */
    fun clearAllDownloads() {
        _downloads.value = emptyList()
        hideDownloadManageDialog()
    }
}
