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
fun KidAudioPlayerScreen(
    navController: NavController,
    bookId: String?,
    playerViewModel: PlayerViewModel = viewModel() // Lấy PlayerViewModel từ parent
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

    val displayBook = book ?: Book("id", "Đang tải...", "...", "", "kid", 0.0)

    // Lấy state từ PlayerViewModel
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val sliderPosition by playerViewModel.currentPosition.collectAsState()

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
                    .blur(radius = 30.dp) // Độ mờ giống ảnh mẫu
            )

            // Lớp phủ tối để làm nổi bật text trắng
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
        }

        // --- LAYER 2: NỘI DUNG CHÍNH ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. TOP BAR (Nút xuống & Logo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Nút thu nhỏ - KHÔNG TẮT NHẠC, chỉ minimize
                IconButton(
                    onClick = {
                        playerViewModel.minimizePlayer() // Thu nhỏ player
                        navController.popBackStack() // Quay lại màn hình trước
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

                // Logo App (Thay vì Fonos thì để RoSach)
                Text(
                    text = "RoSach Kids",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                // Placeholder để cân giữa logo (Button tàng hình)
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Đẩy nội dung chính xuống dưới (giống layout trong ảnh)
            Spacer(modifier = Modifier.weight(1f))

            // 2. THÔNG TIN SÁCH (Ở giữa/dưới)
            Text(
                text = displayBook.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mô tả ngắn / Tác giả (chạy chữ marquee hoặc rút gọn)
            Text(
                text = "${displayBook.title} là bộ truyện duy mỹ và nhân văn...", // Giả lập text dài
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. SLIDER & TIME
            Slider(
                value = sliderPosition,
                onValueChange = { playerViewModel.updatePosition(it) },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "00:43", color = Color.White, fontSize = 12.sp)
                Text(text = "-15:54", color = Color.White, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. NÚT PLAY LỚN - Kết nối với PlayerViewModel
            IconButton(
                onClick = { playerViewModel.togglePlayPause() },
                modifier = Modifier
                    .size(80.dp) // Kích thước lớn
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.Black, // Icon màu đen trên nền trắng
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. NÚT HẸN GIỜ (Dưới cùng)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.NightsStay, // Icon trăng khuyết
                    contentDescription = "Sleep Timer",
                    tint = Color.White,
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