package com.kienvo.fonosclone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.fonosclone.service.AudioPlayerService
import com.kienvo.fonosclone.ui.theme.Yellow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    navController: NavController,
    bookId: String?
) {
    // Dữ liệu sách dựa trên bookId
    val isDacNhanTam = bookId == "2"

    val bookTitle = if (isDacNhanTam) "Đắc Nhân Tâm" else "Cuốn sách khác"
    val bookAuthor = if (isDacNhanTam) "Dale Carnegie" else "Tác giả khác"
    val bookCover = if (isDacNhanTam)
        "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg"
        else ""

    // Audio player state
    val context = LocalContext.current
    val audioService = remember { AudioPlayerService(context) }

    val isPlaying by audioService.isPlaying.collectAsState()
    val isLoading by audioService.isLoading.collectAsState()
    val currentPosition by audioService.currentPosition.collectAsState()
    val duration by audioService.duration.collectAsState()

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Load audio when screen opens
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
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioService.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background với bìa sách blur
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(bookCover)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 40.dp)
        )

        // Overlay tối để text dễ đọc
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Đang phát",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                // Book Cover với shadow đẹp
                Card(
                    modifier = Modifier.size(280.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(bookCover)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Book Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Book Info với style đẹp
                Text(
                    text = bookTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = bookAuthor,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(50.dp))

                // Audio Controls Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Progress Slider
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
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
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
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                            Text(
                                text = formatTime(duration),
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        // Control Buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {

                            // Previous button
                            IconButton(
                                onClick = { /* Previous logic */ },
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.FastRewind,
                                    contentDescription = "Previous",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Play/Pause button - Main
                            Button(
                                onClick = { audioService.togglePlayPause() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Yellow
                                ),
                                shape = CircleShape,
                                modifier = Modifier.size(80.dp),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = Color.Black,
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = Color.Black,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            // Next button
                            IconButton(
                                onClick = { /* Next logic */ },
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    Icons.Default.FastForward,
                                    contentDescription = "Next",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
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
