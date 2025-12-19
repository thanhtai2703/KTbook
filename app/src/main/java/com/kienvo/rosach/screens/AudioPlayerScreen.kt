package com.kienvo.rosach.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.model.BookPart
import com.kienvo.rosach.repository.BookRepository
import com.kienvo.rosach.service.AudioPlayerService
import com.kienvo.rosach.ui.theme.Yellow
import com.kienvo.rosach.viewmodel.BookViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    navController: NavController,
    bookId: String?,
    bookViewModel: BookViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val bookRepository = remember { BookRepository() }

    // State cho sách và parts
    var book by remember { mutableStateOf<Book?>(null) }
    var bookParts by remember { mutableStateOf<List<BookPart>>(emptyList()) }
    var currentPartIndex by remember { mutableIntStateOf(0) }
    var isLoadingData by remember { mutableStateOf(true) }

    // Audio player state
    val audioService = remember { AudioPlayerService(context) }
    val isPlaying by audioService.isPlaying.collectAsState()
    val isLoading by audioService.isLoading.collectAsState()
    val currentPosition by audioService.currentPosition.collectAsState()
    val duration by audioService.duration.collectAsState()
    val error by audioService.error.collectAsState()

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Load sách và parts khi screen mở
    LaunchedEffect(bookId) {
        if (bookId != null) {
            isLoadingData = true
            book = bookViewModel.getBookById(bookId)
            bookParts = bookRepository.getBookParts(bookId)

            // Load audio của part đầu tiên
            if (bookParts.isNotEmpty()) {
                val firstPart = bookParts[0]
                audioService.loadAudioFromUrl(firstPart.audioUrl)
            }
            isLoadingData = false
        }
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

    // Current part info
    val currentPart = if (bookParts.isNotEmpty() && currentPartIndex < bookParts.size) {
        bookParts[currentPartIndex]
    } else null

    Box(modifier = Modifier.fillMaxSize()) {
        // Background với bìa sách blur
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(book?.coverUrl ?: "")
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
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        // Main content
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Đang phát", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->

            if (isLoadingData) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Yellow)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Book cover
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(book?.coverUrl ?: "")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Book Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(280.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Book info
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = book?.title ?: "Đang tải...",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = book?.author ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )

                        // Current part
                        if (currentPart != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentPart.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Yellow,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Progress bar
                    Column(modifier = Modifier.fillMaxWidth()) {
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

                    Spacer(modifier = Modifier.height(24.dp))

                    // Playback controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous/Rewind button
                        IconButton(
                            onClick = { audioService.seekBackward(15000L) },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                Icons.Default.FastRewind,
                                contentDescription = "Rewind",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Play/Pause button
                        FloatingActionButton(
                            onClick = { audioService.togglePlayPause() },
                            containerColor = Yellow,
                            modifier = Modifier.size(72.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Icon(
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        // Next/Forward button
                        IconButton(
                            onClick = { audioService.seekForward(15000L) },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                Icons.Default.FastForward,
                                contentDescription = "Forward",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Error message
                    if (error != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error ?: "",
                            color = Color.Red,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

// Helper function để format thời gian
private fun formatTime(milliseconds: Long): String {
    if (milliseconds <= 0) return "0:00"

    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
