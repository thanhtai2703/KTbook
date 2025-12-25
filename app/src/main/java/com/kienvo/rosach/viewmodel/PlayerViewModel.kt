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

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    val audioService = AudioPlayerService(application)

    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook.asStateFlow()

    private val _showMiniPlayer = MutableStateFlow(false)
    val showMiniPlayer: StateFlow<Boolean> = _showMiniPlayer.asStateFlow()

    // Expose flows from Service
    val isPlaying = audioService.isPlaying
    val isLoading = audioService.isLoading
    val currentPosition = audioService.currentPosition
    val duration = audioService.duration
    val error = audioService.error

    // Persistent Ambient Volumes
    private val _rainVol = MutableStateFlow(0f)
    val rainVol: StateFlow<Float> = _rainVol.asStateFlow()

    private val _windVol = MutableStateFlow(0f)
    val windVol: StateFlow<Float> = _windVol.asStateFlow()

    private val _fireVol = MutableStateFlow(0f)
    val fireVol: StateFlow<Float> = _fireVol.asStateFlow()

    private val _birdsVol = MutableStateFlow(0f)
    val birdsVol: StateFlow<Float> = _birdsVol.asStateFlow()

    // Ambient URLs Template
    private val ambientUrls = mapOf(
        "rain" to "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/Enviroment%2Fcopyright-free-rain-sounds-331497.mp3?alt=media&token=d32cd58c-9f77-4240-baea-f2210a6eac8b",
        "wind" to "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/Enviroment%2Fwind-blowing-sfx-01-423673.mp3?alt=media&token=ed18c622-6c82-4d8c-8a33-e893f2471156",
        "fire" to "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/Enviroment%2Fcampfire-crackling-fireplace-sound-119594.mp3?alt=media&token=a6810534-378f-4211-b774-c256bd3b3678",
        "birds" to "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/Enviroment%2Fmixkit-morning-birds-2472.wav?alt=media&token=073e783a-fdc0-4ae6-8956-99db3683e548"
    )

    init {
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

    /**
     * Ambient Controls
     */
    fun setAmbientType(type: String) {
        val url = ambientUrls[type] ?: return
        val vol = when(type) {
            "rain" -> _rainVol.value
            "wind" -> _windVol.value
            "fire" -> _fireVol.value
            "birds" -> _birdsVol.value
            else -> 0f
        }
        audioService.setAmbientSound(type, url, vol)
    }

    /**
     * Applies a complex mix from AI
     * Config example: {"rain": 0.4, "birds": 0.2}
     */
    fun setAmbientMix(jsonConfig: String) {
        try {
            val types = listOf("rain", "wind", "fire", "birds")
            stopAmbient() 
            
            types.forEach { type ->
                if (jsonConfig.contains("\"$type\"")) {
                    val startIndex = jsonConfig.indexOf("\"$type\"") + type.length + 3
                    var endIndex = jsonConfig.indexOf(",", startIndex)
                    if (endIndex == -1) endIndex = jsonConfig.indexOf("}", startIndex)
                    
                    val valStr = jsonConfig.substring(startIndex, endIndex).replace(":", "").trim()
                    val volume = valStr.toFloatOrNull() ?: 0f
                    
                    if (volume > 0) {
                        updateAmbientVol(type, volume)
                        setAmbientType(type)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("PlayerVM", "Error parsing AI mix: ${e.message}")
        }
    }

    fun updateAmbientVol(type: String, volume: Float) {
        when(type) {
            "rain" -> _rainVol.value = volume
            "wind" -> _windVol.value = volume
            "fire" -> _fireVol.value = volume
            "birds" -> _birdsVol.value = volume
        }
        audioService.setAmbientVolume(type, volume)
    }

    fun stopAmbient() {
        audioService.stopAmbient()
        _rainVol.value = 0f
        _windVol.value = 0f
        _fireVol.value = 0f
        _birdsVol.value = 0f
    }

    fun loadUrl(url: String) = audioService.loadAudioFromUrl(url)
    fun togglePlayPause() = audioService.togglePlayPause()
    fun seekTo(position: Long) = audioService.seekTo(position)
    fun updatePosition(percent: Float) {
        val total = audioService.duration.value
        if (total > 0) audioService.seekTo((percent * total).toLong())
    }
    fun seekForward() = audioService.seekForward()
    fun seekBackward() = audioService.seekBackward()
    fun closePlayer() { audioService.pause(); _currentBook.value = null; _showMiniPlayer.value = false }
    fun minimizePlayer() { _showMiniPlayer.value = true }
    fun maximizePlayer() { _showMiniPlayer.value = false }

    override fun onCleared() {
        audioService.release()
        super.onCleared()
    }
}
