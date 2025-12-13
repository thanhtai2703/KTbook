package com.kienvo.fonosclone.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kienvo.fonosclone.service.AudioPlayerService
import com.kienvo.fonosclone.ui.theme.Yellow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerWidget(
    bookTitle: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioService = remember { AudioPlayerService(context) }

    val isPlaying by audioService.isPlaying.collectAsState()
    val isLoading by audioService.isLoading.collectAsState()
    val currentPosition by audioService.currentPosition.collectAsState()
    val duration by audioService.duration.collectAsState()

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Load audio when composable is first created
    LaunchedEffect(Unit) {
        audioService.loadDacNhanTamAudio()
    }

    // Update slider position
    LaunchedEffect(currentPosition, duration) {
        if (!isDragging && duration > 0) {
            sliderPosition = currentPosition.toFloat() / duration.toFloat()
        }
    }

    // Update position every second when playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000)
            // Force update positions
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioService.release()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "🎧 Audiobook",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = bookTitle,
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Slider
            Column {
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        isDragging = true
                        sliderPosition = newValue
                    },
                    onValueChangeFinished = {
                        isDragging = false
                        val newPosition = (sliderPosition * duration).toLong()
                        audioService.seekTo(newPosition)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Yellow,
                        activeTrackColor = Yellow,
                        inactiveTrackColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Time labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatTime(duration),
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Play/Pause Button
            Button(
                onClick = { audioService.togglePlayPause() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow
                ),
                shape = CircleShape,
                modifier = Modifier.size(64.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(timeMs: Long): String {
    if (timeMs <= 0) return "00:00"

    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d", minutes, seconds)
}
