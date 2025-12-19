package com.kienvo.rosach.service

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(UnstableApi::class)
class AudioPlayerService(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Fallback URL cho demo (Đắc Nhân Tâm)
    private val dacNhanTamAudioUrl = "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/DacNhanTam%2Fdac-nhan.mp3?alt=media&token=7673b069-8efe-4b4d-a9de-35ae516e47fd"

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _isLoading.value = playbackState == Player.STATE_BUFFERING

                    when (playbackState) {
                        Player.STATE_READY -> {
                            _duration.value = duration
                            _error.value = null
                        }
                        Player.STATE_ENDED -> {
                            _isPlaying.value = false
                        }
                    }
                    updatePlaybackState()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    _error.value = "Lỗi phát audio: ${error.message}"
                    _isLoading.value = false
                }
            })
        }
    }

    /**
     * Load audio từ URL bất kỳ
     */
    fun loadAudioFromUrl(audioUrl: String) {
        if (audioUrl.isEmpty()) {
            _error.value = "URL audio không hợp lệ"
            return
        }

        try {
            _isLoading.value = true
            _error.value = null

            exoPlayer?.let { player ->
                val mediaItem = MediaItem.fromUri(audioUrl)
                player.setMediaItem(mediaItem)
                player.prepare()
            }
        } catch (e: Exception) {
            _error.value = "Không thể load audio: ${e.message}"
            _isLoading.value = false
        }
    }

    /**
     * Load audio "Đắc Nhân Tâm" (fallback cho demo)
     */
    fun loadDacNhanTamAudio() {
        loadAudioFromUrl(dacNhanTamAudioUrl)
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        _currentPosition.value = position
    }

    fun togglePlayPause() {
        exoPlayer?.let { player ->
            if (player.isPlaying) {
                pause()
            } else {
                play()
            }
        }
    }

    fun seekForward(milliseconds: Long = 15000L) {
        exoPlayer?.let { player ->
            val newPosition = (player.currentPosition + milliseconds).coerceAtMost(player.duration)
            seekTo(newPosition)
        }
    }

    fun seekBackward(milliseconds: Long = 15000L) {
        exoPlayer?.let { player ->
            val newPosition = (player.currentPosition - milliseconds).coerceAtLeast(0L)
            seekTo(newPosition)
        }
    }

    private fun updatePlaybackState() {
        exoPlayer?.let { player ->
            _currentPosition.value = player.currentPosition
            _duration.value = player.duration
        }
    }

    fun getCurrentPosition(): Long = exoPlayer?.currentPosition ?: 0L

    fun getDuration(): Long = exoPlayer?.duration ?: 0L

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
