package com.kienvo.rosach.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.service.AudioPlayerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Global Player ViewModel that manages the AudioPlayerService
 */
class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val audioService = AudioPlayerService(application)

    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook.asStateFlow()

    private val _showMiniPlayer = MutableStateFlow(false)
    val showMiniPlayer: StateFlow<Boolean> = _showMiniPlayer.asStateFlow()

    // Expose flows from service
    val isPlaying = audioService.isPlaying
    val isLoading = audioService.isLoading
    val currentPosition = audioService.currentPosition
    val duration = audioService.duration
    val error = audioService.error

    init {
        // Periodic position update while playing
        viewModelScope.launch {
            while (true) {
                if (audioService.isPlaying.value) {
                    audioService.updatePlaybackState()
                }
                delay(1000)
            }
        }
    }

    fun playBook(book: Book, audioUrl: String? = null) {
        _currentBook.value = book
        _showMiniPlayer.value = true
        
        audioUrl?.let {
            audioService.loadAudioFromUrl(it)
            audioService.play()
        }
    }

    fun loadUrl(url: String) {
        audioService.loadAudioFromUrl(url)
    }

    fun togglePlayPause() {
        audioService.togglePlayPause()
    }

    fun seekTo(position: Long) {
        audioService.seekTo(position)
    }

    fun updatePosition(percent: Float) {
        val total = audioService.duration.value
        if (total > 0) {
            audioService.seekTo((percent * total).toLong())
        }
    }

    fun seekForward() = audioService.seekForward()
    fun seekBackward() = audioService.seekBackward()

    fun closePlayer() {
        audioService.pause()
        _currentBook.value = null
        _showMiniPlayer.value = false
    }

    fun minimizePlayer() {
        _showMiniPlayer.value = true
    }

    fun maximizePlayer() {
        _showMiniPlayer.value = false
    }

    override fun onCleared() {
        audioService.release()
        super.onCleared()
    }
}