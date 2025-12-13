package com.kienvo.fonosclone.service

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

    // URL audio "Đắc Nhân Tâm" từ Firebase
    private val dacNhanTamAudioUrl = "https://firebasestorage.googleapis.com/v0/b/rosach-5d3e8.firebasestorage.app/o/dac-nhan.mp3?alt=media&token=4f557391-d2b3-4ed9-8cc3-009c6916faac"

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _isLoading.value = playbackState == Player.STATE_BUFFERING
                    updatePlaybackState()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }
            })
        }
    }

    fun loadDacNhanTamAudio() {
        exoPlayer?.let { player ->
            val mediaItem = MediaItem.fromUri(dacNhanTamAudioUrl)
            player.setMediaItem(mediaItem)
            player.prepare()

            // Update duration when ready
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = player.duration
                    }
                }
            })
        }
    }

    fun play() {
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
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
