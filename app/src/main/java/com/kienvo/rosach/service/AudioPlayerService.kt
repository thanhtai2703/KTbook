package com.kienvo.rosach.service

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@OptIn(UnstableApi::class)
class AudioPlayerService(private val context: Context) {

    private var mainPlayer: ExoPlayer? = null
    
    // Map to hold players for each ambient sound type
    private val ambientPlayers = mutableMapOf<String, ExoPlayer>()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _currentUrl = MutableStateFlow<String?>(null)
    val currentUrl = _currentUrl.asStateFlow()

    init {
        initializeMainPlayer()
    }

    private fun initializeMainPlayer() {
        if (mainPlayer != null) return
        mainPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    _isLoading.value = state == Player.STATE_BUFFERING
                    if (state == Player.STATE_READY) _duration.value = duration
                    if (state == Player.STATE_ENDED) _isPlaying.value = false
                }
                override fun onIsPlayingChanged(playing: Boolean) { _isPlaying.value = playing }
            })
        }
    }

    private fun getOrCreateAmbientPlayer(type: String): ExoPlayer {
        return ambientPlayers.getOrPut(type) {
            ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                volume = 0f
            }
        }
    }

    fun loadAudioFromUrl(audioUrl: String) {
        if (audioUrl == _currentUrl.value) return
        try {
            _currentUrl.value = audioUrl
            mainPlayer?.let { player ->
                val mediaItem = MediaItem.fromUri(audioUrl)
                player.setMediaItem(mediaItem)
                player.prepare()
            }
        } catch (e: Exception) {
            _error.value = e.message
        }
    }

    /**
     * Controls a specific ambient sound stream
     */
    fun setAmbientSound(type: String, url: String, volume: Float) {
        try {
            val player = getOrCreateAmbientPlayer(type)
            val currentUri = player.currentMediaItem?.localConfiguration?.uri?.toString()
            
            // If volume is > 0, ensure it's playing the right content
            if (volume > 0f) {
                if (currentUri != url) {
                    val mediaItem = MediaItem.fromUri(url)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                }
                if (!player.isPlaying) {
                    player.play()
                }
            } else {
                // If volume is 0, just pause to save battery
                if (player.isPlaying) {
                    player.pause()
                }
            }
            player.volume = volume.coerceIn(0f, 1f)
        } catch (e: Exception) {
            android.util.Log.e("AudioService", "Error setting ambient $type: ${e.message}")
        }
    }

    fun setAmbientVolume(type: String, volume: Float) {
        val player = ambientPlayers[type]
        if (player != null) {
            player.volume = volume.coerceIn(0f, 1f)
            if (volume > 0f && !player.isPlaying) {
                player.play()
            } else if (volume <= 0f && player.isPlaying) {
                player.pause()
            }
        }
    }

    fun stopAmbient() {
        ambientPlayers.values.forEach { 
            it.pause()
            it.volume = 0f
        }
    }

    fun play() = mainPlayer?.play()
    fun pause() = mainPlayer?.pause()
    fun togglePlayPause() { if (mainPlayer?.isPlaying == true) pause() else play() }
    fun seekTo(pos: Long) = mainPlayer?.seekTo(pos)
    fun seekForward() = mainPlayer?.let { it.seekTo((it.currentPosition + 15000).coerceAtMost(it.duration)) }
    fun seekBackward() = mainPlayer?.let { it.seekTo((it.currentPosition - 15000).coerceAtLeast(0)) }

    fun updatePlaybackState() {
        mainPlayer?.let {
            _currentPosition.value = it.currentPosition
            _duration.value = it.duration
        }
    }

    fun release() {
        mainPlayer?.release()
        ambientPlayers.values.forEach { it.release() }
        ambientPlayers.clear()
        mainPlayer = null
    }
}