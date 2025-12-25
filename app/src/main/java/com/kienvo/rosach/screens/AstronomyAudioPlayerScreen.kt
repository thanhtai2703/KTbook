package com.kienvo.rosach.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
import com.kienvo.rosach.data.SampleData
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.viewmodel.PlayerViewModel

@Composable
fun AstronomyAudioPlayerScreen(
    navController: NavController,
    bookId: String?,
    playerViewModel: PlayerViewModel = viewModel()
) {
    // 1. Lấy dữ liệu sách
    var book by remember { mutableStateOf<Book?>(null) }

    LaunchedEffect(bookId) {
        if (bookId != null) {
            book = SampleData.allBooks.find { it.id == bookId }
            // Cập nhật vào PlayerViewModel khi mở màn hình
            book?.let { playerViewModel.playBook(it) }
        }
    }

    val displayBook = book ?: Book("id", "Đang tải...", "...", "", "astronomy", 0.0)

    // Lấy state từ PlayerViewModel
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val sliderPosition by playerViewModel.currentPosition.collectAsState()

    // Màu sắc cho theme thiên văn
    val AstronomyPurple = Color(0xFF7E57C2)
    val AstronomyBlue = Color(0xFF5E35B1)

    Box(modifier = Modifier.fillMaxSize()) {

        // --- LAYER 1: BACKGROUND ẢNH BÌA MỜ ---
        if (displayBook.coverUrl.toString().isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(displayBook.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 30.dp)
            )

            // Lớp phủ tối với màu tím cho theme không gian
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A2E)))
        }

        // --- LAYER 2: NỘI DUNG CHÍNH ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. TOP BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Nút thu nhỏ - KHÔNG TẮT NHẠC, chỉ minimize
                IconButton(
                    onClick = {
                        playerViewModel.minimizePlayer()
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize",
                        tint = Color.White
                    )
                }

                // Logo App
                Text(
                    text = "RoSach Astronomy",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Placeholder để cân giữa logo
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Đẩy nội dung chính xuống dưới
            Spacer(modifier = Modifier.weight(1f))

            // 2. THÔNG TIN SÁCH
            Text(
                text = displayBook.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tác giả
            Text(
                text = "Tác giả: ${displayBook.author}",
                color = AstronomyPurple,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mô tả ngắn
            Text(
                text = "Khám phá những bí ẩn của vũ trụ qua lăng kính khoa học...",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. SLIDER & TIME
            Slider(
                value = sliderPosition,
                onValueChange = { playerViewModel.updatePosition(it) },
                colors = SliderDefaults.colors(
                    thumbColor = AstronomyPurple,
                    activeTrackColor = AstronomyPurple,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(sliderPosition),
                    color = Color.White,
                    fontSize = 12.sp
                )
                Text(
                    text = formatRemainingTime(sliderPosition),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. NÚT PLAY LỚN với gradient theme không gian
            IconButton(
                onClick = { playerViewModel.togglePlayPause() },
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = AstronomyBlue,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. NÚT HẸN GIỜ
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NightsStay,
                    contentDescription = "Sleep Timer",
                    tint = AstronomyPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hẹn giờ",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// Helper functions để format thời gian
private fun formatTime(position: Float): String {
    val totalSeconds = (position * 3600).toInt() // Giả sử slider 0-1 = 0-60 phút
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

private fun formatRemainingTime(position: Float): String {
    val totalSeconds = ((1f - position) * 3600).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("-%02d:%02d", minutes, seconds)
}
