package com.kienvo.rosach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.repository.BookRepository
import com.kienvo.rosach.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SortOption {
    NAME_ASC,       // Tên A-Z
    NAME_DESC,      // Tên Z-A
    AUTHOR_ASC,     // Tác giả A-Z
    AUTHOR_DESC,    // Tác giả Z-A
    RECENTLY_ADDED  // Thêm gần đây
}

class LibraryViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val bookRepository = BookRepository()
    private val auth = FirebaseAuth.getInstance()

    // State cho các tab trong Library
    private val _currentlyListening = MutableStateFlow<List<Book>>(emptyList())
    val currentlyListening: StateFlow<List<Book>> = _currentlyListening.asStateFlow()

    private val _favorites = MutableStateFlow<List<Book>>(emptyList())
    val favorites: StateFlow<List<Book>> = _favorites.asStateFlow()

    private val _history = MutableStateFlow<List<Book>>(emptyList())
    val history: StateFlow<List<Book>> = _history.asStateFlow()

    private val _downloads = MutableStateFlow<List<Book>>(emptyList())
    val downloads: StateFlow<List<Book>> = _downloads.asStateFlow()

    // State cho progress của sách đang nghe (bookId -> progress)
    private val _listeningProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val listeningProgress: StateFlow<Map<String, Float>> = _listeningProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _favoriteSortOption = MutableStateFlow(SortOption.RECENTLY_ADDED)
    val favoriteSortOption: StateFlow<SortOption> = _favoriteSortOption.asStateFlow()

    private val _showDownloadManageDialog = MutableStateFlow(false)
    val showDownloadManageDialog: StateFlow<Boolean> = _showDownloadManageDialog.asStateFlow()

    init {
        loadLibraryData()
    }

    /**
     * Load toàn bộ dữ liệu cho thư viện
     */
    fun loadLibraryData() {
        val userId = auth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Load Profile
                val profileResult = userRepository.getUserProfile(userId)
                profileResult.onSuccess { profile ->
                    if (profile != null) {
                        // 2. Load Favorites
                        val favoriteBooks = bookRepository.getBooksByIds(profile.favoriteBookIds)
                        _favorites.value = favoriteBooks

                        // 3. Load History
                        val historyBooks = bookRepository.getBooksByIds(profile.listeningHistory.map { it.bookId })
                        _history.value = historyBooks

                        // 4. Load Currently Listening
                        val listeningRecords = profile.listeningHistory.filter { it.progress > 0 && it.progress < 1.0f }
                        val listeningBooks = bookRepository.getBooksByIds(listeningRecords.map { it.bookId })
                        _currentlyListening.value = listeningBooks

                        // 5. Update Progress Map
                        val progressMap = profile.listeningHistory.associate { it.bookId to it.progress }
                        _listeningProgress.value = progressMap
                    }
                }
            } catch (e: Exception) {
                // Log error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromCurrentlyListening(book: Book) {
        // Local update
        _currentlyListening.value = _currentlyListening.value.filter { it.id != book.id }
        // Remote update
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.removeHistoryItem(userId, book.id)
        }
    }

    fun addToCurrentlyListening(book: Book, progress: Float = 0f) {
        if (_currentlyListening.value.none { it.id == book.id }) {
            _currentlyListening.value = listOf(book) + _currentlyListening.value
        }
        // Update history in Firestore too
        addToHistory(book)
    }

    fun removeFromFavorites(book: Book) {
        _favorites.value = _favorites.value.filter { it.id != book.id }
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.removeFavoriteBook(userId, book.id)
        }
    }

    fun addToFavorites(book: Book) {
        if (_favorites.value.none { it.id == book.id }) {
            _favorites.value = listOf(book) + _favorites.value
        }
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.addFavoriteBook(userId, book.id)
        }
    }

    fun toggleFavorite(book: Book) {
        if (_favorites.value.any { it.id == book.id }) {
            removeFromFavorites(book)
        } else {
            addToFavorites(book)
        }
    }

    fun removeFromHistory(book: Book) {
        _history.value = _history.value.filter { it.id != book.id }
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.removeHistoryItem(userId, book.id)
        }
    }

    fun addToHistory(book: Book) {
        val filtered = _history.value.filter { it.id != book.id }
        _history.value = listOf(book) + filtered
        
        // Remote sync
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val record = com.kienvo.rosach.data.ListeningRecord(
                bookId = book.id,
                bookTitle = book.title,
                lastPosition = 0L,
                duration = 0L,
                progress = 0.1f, // Mặc định là đã bắt đầu nghe
                lastListenedAt = System.currentTimeMillis()
            )
            userRepository.updateListeningHistory(userId, record)
        }
    }

    fun clearAllHistory() {
        _history.value = emptyList()
    }

    fun removeFromDownloads(book: Book) {
        _downloads.value = _downloads.value.filter { it.id != book.id }
    }

    fun addToDownloads(book: Book) {
        if (_downloads.value.none { it.id == book.id }) {
            _downloads.value = listOf(book) + _downloads.value
        }
    }

    fun getTotalDownloadSize(): String {
        val totalMB = _downloads.value.size * 42 
        return "$totalMB MB"
    }

    fun sortFavorites(sortOption: SortOption) {
        _favoriteSortOption.value = sortOption
        _favorites.value = when (sortOption) {
            SortOption.NAME_ASC -> _favorites.value.sortedBy { it.title.lowercase() }
            SortOption.NAME_DESC -> _favorites.value.sortedByDescending { it.title.lowercase() }
            SortOption.AUTHOR_ASC -> _favorites.value.sortedBy { it.author.lowercase() }
            SortOption.AUTHOR_DESC -> _favorites.value.sortedByDescending { it.author.lowercase() }
            SortOption.RECENTLY_ADDED -> _favorites.value 
        }
    }

    fun showDownloadManageDialog() {
        _showDownloadManageDialog.value = true
    }

    fun hideDownloadManageDialog() {
        _showDownloadManageDialog.value = false
    }

    fun clearAllDownloads() {
        _downloads.value = emptyList()
        hideDownloadManageDialog()
    }
}