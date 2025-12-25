package com.kienvo.rosach.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.kienvo.rosach.viewmodel.PlayerViewModel
import java.util.Locale

/**
 * Unified Player Screen for all types of books (Normal, Kids, Astronomy)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    navController: NavController,
    bookId: String?,
    bookViewModel: BookViewModel = viewModel(),
    playerViewModel: PlayerViewModel = viewModel()
) {
    val context = LocalContext.current
    val bookRepository = remember { BookRepository() }

    // State
    var book by remember { mutableStateOf<Book?>(null) }
    var bookParts by remember { mutableStateOf<List<BookPart>>(emptyList()) }
    var currentPartIndex by remember { mutableIntStateOf(0) }
    var isLoadingData by remember { mutableStateOf(true) }
    var showPartsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Audio player service
    val audioService = remember { AudioPlayerService(context) }
    val isPlaying by audioService.isPlaying.collectAsState()
    val isAudioLoading by audioService.isLoading.collectAsState()
    val currentPosition by audioService.currentPosition.collectAsState()
    val duration by audioService.duration.collectAsState()
    val audioError by audioService.error.collectAsState()

    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // DYNAMIC THEME BASED ON BOOK TYPE
    val accentColor = when (book?.type) {
        "kid" -> Color(0xFFFFB74D) // Orange for kids
        "astronomy" -> Color(0xFF7E57C2) // Purple for space
        else -> Yellow // Default yellow
    }

    val playerBackground = when (book?.type) {
        "kid" -> Brush.verticalGradient(listOf(Color(0xFFE57373).copy(0.4f), Color.Black))
        "astronomy" -> Brush.verticalGradient(listOf(Color(0xFF283593).copy(0.4f), Color.Black))
        else -> Brush.verticalGradient(listOf(Color.Black.copy(0.7f), Color.Black.copy(0.9f)))
    }

    // Helper to load part
    fun loadPart(index: Int) {
        if (index in bookParts.indices) {
            currentPartIndex = index
            audioService.loadAudioFromUrl(bookParts[index].audioUrl)
            audioService.play()
        }
    }

    // Initialize
    LaunchedEffect(bookId) {
        if (bookId != null) {
            isLoadingData = true
            val fetchedBook = bookViewModel.getBookById(bookId)
            book = fetchedBook
            val fetchedParts = bookRepository.getBookParts(bookId)
            bookParts = fetchedParts

            fetchedBook?.let { playerViewModel.playBook(it) }

            if (fetchedParts.isNotEmpty()) {
                currentPartIndex = 0
                audioService.loadAudioFromUrl(fetchedParts[0].audioUrl)
            }
            isLoadingData = false
        }
    }

    // Sync
    LaunchedEffect(isPlaying) {
        book?.let { if (isPlaying) playerViewModel.playBook(it) }
    }

    LaunchedEffect(currentPosition, duration) {
        if (!isDragging && duration > 0) {
            sliderPosition = currentPosition.toFloat() / duration.toFloat()
            playerViewModel.updatePosition(sliderPosition)
        }
    }

    DisposableEffect(Unit) {
        onDispose { audioService.release() }
    }

    val currentPart = if (bookParts.isNotEmpty() && currentPartIndex < bookParts.size) {
        bookParts[currentPartIndex]
    } else null

    Box(modifier = Modifier.fillMaxSize()) {
        // LAYER 1: BLURRED BACKGROUND
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(book?.coverUrl ?: "").crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(radius = 40.dp)
        )
        Box(modifier = Modifier.fillMaxSize().background(playerBackground))

        // LAYER 2: CONTENT
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(if (book?.type == "kid") "RoSach Kids" else if (book?.type == "astronomy") "RoSach Astronomy" else "Đang phát", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { playerViewModel.minimizePlayer(); navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showPartsSheet = true }) {
                            Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = "Chapters", tint = Color.White)
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.NightsStay, contentDescription = "Sleep Timer", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (isLoadingData) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = accentColor)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Cover
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(book?.coverUrl ?: "").crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(if (book?.type == "kid") 240.dp else 280.dp).clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Info
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = book?.title ?: "Đang tải...", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = book?.author ?: "", style = MaterialTheme.typography.bodyLarge, color = Color.LightGray, textAlign = TextAlign.Center)
                        if (currentPart != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = currentPart.title, style = MaterialTheme.typography.bodyMedium, color = accentColor, textAlign = TextAlign.Center)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Progress
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it; isDragging = true },
                            onValueChangeFinished = {
                                isDragging = false
                                val newPos = (sliderPosition * duration).toLong()
                                audioService.seekTo(newPos)
                            },
                            colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor, inactiveTrackColor = Color.Gray),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = formatTime(currentPosition), color = Color.LightGray, fontSize = 12.sp)
                            Text(text = formatTime(duration), color = Color.LightGray, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Controls
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (currentPartIndex > 0) loadPart(currentPartIndex - 1) }, enabled = currentPartIndex > 0) {
                            Icon(Icons.Default.SkipPrevious, null, tint = if (currentPartIndex > 0) Color.White else Color.Gray, modifier = Modifier.size(32.dp))
                        }
                        IconButton(onClick = { audioService.seekBackward() }) {
                            Icon(Icons.Default.FastRewind, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        FloatingActionButton(onClick = { audioService.togglePlayPause() }, containerColor = accentColor, modifier = Modifier.size(72.dp), shape = CircleShape) {
                            if (isAudioLoading) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(32.dp))
                            else Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(40.dp))
                        }
                        IconButton(onClick = { audioService.seekForward() }) {
                            Icon(Icons.Default.FastForward, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                        IconButton(onClick = { if (currentPartIndex < bookParts.size - 1) loadPart(currentPartIndex + 1) }, enabled = currentPartIndex < bookParts.size - 1) {
                            Icon(Icons.Default.SkipNext, null, tint = if (currentPartIndex < bookParts.size - 1) Color.White else Color.Gray, modifier = Modifier.size(32.dp))
                        }
                    }

                    if (audioError != null) Text(text = audioError!!, color = Color.Red, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp))
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Chapters Sheet
        if (showPartsSheet) {
            ModalBottomSheet(onDismissRequest = { showPartsSheet = false }, sheetState = sheetState, containerColor = Color(0xFF1E1E1E), contentColor = Color.White) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 40.dp)) {
                    Text("Danh sách chương", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    LazyColumn {
                        items(bookParts.size) { index ->
                            val p = bookParts[index]
                            val sel = index == currentPartIndex
                            Row(modifier = Modifier.fillMaxWidth().clickable { loadPart(index); showPartsSheet = false }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${index + 1}", color = if (sel) accentColor else Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(p.title, color = if (sel) accentColor else Color.White, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal)
                                    Text(p.duration, color = Color.Gray, fontSize = 12.sp)
                                }
                                if (sel) Icon(Icons.Default.PlayCircle, null, tint = accentColor)
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "0:00"
    val s = ms / 1000
    return String.format(Locale.getDefault(), "%d:%02d", s / 60, s % 60)
}
