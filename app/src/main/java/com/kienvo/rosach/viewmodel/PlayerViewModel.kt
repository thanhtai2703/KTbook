package com.kienvo.rosach.viewmodel

import androidx.lifecycle.ViewModel
import com.kienvo.rosach.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel quản lý trạng thái phát nhạc toàn cục
 * Dùng chung cho tất cả các màn hình
 */
class PlayerViewModel : ViewModel() {

    // Trạng thái hiện tại của player
    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0f) // 0.0 -> 1.0
    val currentPosition: StateFlow<Float> = _currentPosition.asStateFlow()

    private val _showMiniPlayer = MutableStateFlow(false)
    val showMiniPlayer: StateFlow<Boolean> = _showMiniPlayer.asStateFlow()

    // Phát sách mới
    fun playBook(book: Book) {
        _currentBook.value = book
        _isPlaying.value = true
        _showMiniPlayer.value = true
        _currentPosition.value = 0f
    }

    // Toggle play/pause
    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    // Đóng player hoàn toàn
    fun closePlayer() {
        _currentBook.value = null
        _isPlaying.value = false
        _showMiniPlayer.value = false
        _currentPosition.value = 0f
    }

    // Thu nhỏ player (không dừng nhạc)
    fun minimizePlayer() {
        _showMiniPlayer.value = true
    }

    // Mở player toàn màn hình
    fun maximizePlayer() {
        _showMiniPlayer.value = false
    }

    // Cập nhật vị trí phát
    fun updatePosition(position: Float) {
        _currentPosition.value = position.coerceIn(0f, 1f)
    }
}

