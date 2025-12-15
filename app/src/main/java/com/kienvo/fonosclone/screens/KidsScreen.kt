package com.kienvo.fonosclone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

// Model giả lập
data class KidStory(
    val title: String,
    val imageUrl: String,
    val duration: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsScreen(navController: NavController) {
    // 1. MÀU SẮC GRADIENT (Hồng cam -> Xanh ngọc -> Xanh lá)
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF8A80),
            Color(0xFFF48FB1),
            Color(0xFF80DEEA),
            Color(0xFF69F0AE)
        )
    )

    // Data giả lập
    val stories = listOf(
        KidStory("Thần mộng mơ", "https://bookhunterlyceum.org/wp-content/uploads/2024/03/84.png", "16 phút"),
        KidStory("Người cung trăng", "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1344266319i/10434440.jpg", "19 phút"),
        KidStory("Câu lạc bộ Phiêu lưu", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1500647871l/35720456._SY475_.jpg", "32 phút"),
        KidStory("Những bức thư của Họa My", "https://picsum.photos/300/300", "29 phút"),
        KidStory("Hành trình về phương Đông", "https://picsum.photos/301/301", "45 phút"),
        KidStory("Khu vườn bí mật", "https://picsum.photos/302/302", "12 phút"),
        KidStory("Hoàng tử bé", "https://picsum.photos/303/303", "50 phút"),
        KidStory("Dế mèn phiêu lưu ký", "https://picsum.photos/304/304", "40 phút")
    )

    // [LỚP 1] BACKGROUND CHUNG CHO TOÀN BỘ MÀN HÌNH
    // Đây là mấu chốt: Background nằm ở Box cha, bao trùm tất cả
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Họa tiết ngôi sao (Trang trí nền)
        // Những ngôi sao này sẽ hiển thị xuyên qua TopAppBar vì TopAppBar trong suốt
        Icon(
            imageVector = Icons.Default.Star, contentDescription = null,
            tint = Color.White.copy(alpha = 0.15f),
            modifier = Modifier.size(280.dp).align(Alignment.TopEnd).offset(x = 60.dp, y = (-40).dp)
        )
        Icon(
            imageVector = Icons.Default.Star, contentDescription = null,
            tint = Color.White.copy(alpha = 0.1f),
            modifier = Modifier.size(150.dp).align(Alignment.TopStart).offset(x = (-40).dp, y = 120.dp)
        )

        // [LỚP 2] BỐ CỤC CHIA CẮT (COLUMN)
        // Thay vì dùng Scaffold, ta dùng Column để chia màn hình thành 2 phần rõ rệt
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding() // Tránh tai thỏ
        ) {
            // --- PHẦN 1: HEADER CỐ ĐỊNH (Transparent) ---
            // Phần này chiếm không gian riêng, không đè lên Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // Tạo khoảng cách nhỏ với Grid
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hàng nút Back
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }

                // Tiêu đề to (Nằm ngay trên nền sao)
                Text(
                    text = "Truyện thiếu nhi",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

            }

            // --- PHẦN 2: GRID CUỘN (Content) ---
            // Sử dụng weight(1f) để Grid chiếm hết phần không gian CÒN LẠI bên dưới Header
            // Khi cuộn, nội dung sẽ bị cắt (clip) ngay tại mép trên của Grid (tức là mép dưới của Header)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // [QUAN TRỌNG NHẤT] Đẩy Grid xuống dưới và giới hạn vùng cuộn
            ) {

                item(span = {GridItemSpan(maxLineSpan)}) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Những câu chuyện đẹp và hay cho bé",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Tiêu đề section
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Tất cả truyện thiếu nhi",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(stories) { story ->
                    KidStoryItem(story)
                }

                // Spacer dưới cùng để lướt không bị cấn
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun KidStoryItem(story: KidStory) {
    Column {
        // Ảnh bìa bo tròn
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(story.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Hình vuông
                .clip(RoundedCornerShape(16.dp)) // Bo tròn mạnh hơn sách thường
                .background(Color.Black.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tên truyện
        Text(
            text = story.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Thời lượng (Icon + Text)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Schedule,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = story.duration,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
        }
    }
}