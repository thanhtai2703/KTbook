package com.kienvo.rosach.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.data.SampleData
import com.kienvo.rosach.model.Book
import java.util.Locale

// Giữ nguyên các màu sắc đã định nghĩa
val Background = Brush.verticalGradient(listOf(
    Color(0xFF2E3A4A),
    Color(0xFF28323E),
    Color(0xFF222933)
))
val TextGrey = Color(0xFF9AA3AE)
val GreenGift = Color(0xFF7CB342)
val StarYellow = Color(0xFFFFC107)
val RatingBarBg = Color(0xFF2B323D)

@Composable
fun SelfHelpScreen(navController: NavController) {

    // 1. Thêm State để lưu trạng thái View (Grid hay List)
    var isGridView by remember { mutableStateOf(false) }

    val books: List<Book> = remember {
        SampleData.psychologyEbooks + SampleData.lifestyleEbooks + SampleData.psychologyEbooks
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Background)
            .statusBarsPadding()
    ) {
        // --- Header (Giữ nguyên) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.1f), shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sách Self-Help",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Container Nội dung ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFF1B1C20))
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))

                // 2. Section "Tất cả sách" + Icon Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tất cả sách",
                        color = TextGrey,
                        fontSize = 16.sp
                    )

                    // Nút chuyển đổi View
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            // Logic đổi Icon: Đang Grid thì hiện List, đang List thì hiện Grid
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.List else Icons.Filled.GridView,
                            contentDescription = "Switch View",
                            tint = TextGrey,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 3. Hiển thị nội dung dựa theo state isGridView
                if (isGridView) {
                    // --- GRID VIEW ---
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // 2 Cột
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp), // Khoảng cách ngang
                        verticalArrangement = Arrangement.spacedBy(24.dp)   // Khoảng cách dọc
                    ) {
                        items(books) { book ->
                            BookGridItem(
                                title = book.title,
                                author = book.author,
                                coverUrl = book.coverUrl,
                                rating = book.rating,
                                reviewCount = 104
                            )
                        }
                    }
                } else {
                    // --- LIST VIEW (Code cũ) ---
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(books) { book ->
                            BookListItem(
                                title = book.title,
                                author = book.author,
                                coverUrl = book.coverUrl,
                                rating = book.rating,
                                reviewCount = 104,
                                onClick = {}
                            )
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- Composable hiển thị dạng Grid (Giống ảnh mẫu 2) ---
@Composable
private fun BookGridItem(
    title: String,
    author: String,
    coverUrl: String,
    rating: Double,
    reviewCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ }
    ) {
        // Dùng Card để bo góc cả Ảnh và Thanh Rating bên dưới
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = RatingBarBg), // Màu nền của thanh rating
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp) // Tăng chiều cao tổng thể
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Ảnh bìa (Chiếm phần lớn diện tích)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Đẩy thanh rating xuống đáy, ảnh chiếm hết chỗ trống
                )

                // 2. Thanh Rating (Nằm dưới ảnh, không đè lên)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = StarYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f (%d)", rating, reviewCount),
                            color = TextGrey,
                            fontSize = 12.sp
                        )
                    }

                    // Icon Gift
                    Icon(
                        imageVector = Icons.Filled.CardGiftcard,
                        contentDescription = "Free",
                        tint = GreenGift,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tên sách (Chỉ hiện 1 dòng + ...)
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 1, // Giới hạn 1 dòng như mẫu
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Tên tác giả
        Text(
            text = author,
            color = TextGrey,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// --- BookListItem (Code cũ, giữ nguyên để dùng cho List View) ---
@Composable
private fun BookListItem(
    title: String,
    author: String,
    coverUrl: String,
    rating: Double,
    reviewCount: Int,
    onClick: () -> Unit
) {
    // (Giữ nguyên code BookListItem cũ của bạn ở đây)
    // Tôi copy lại cho đầy đủ để bạn dễ paste
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = author,
                color = TextGrey,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = StarYellow, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    text = String.format(Locale.US, "%.1f (%d)", rating, reviewCount),
                    color = TextGrey,
                    fontSize = 13.sp
                )
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.width(1.dp).height(12.dp).background(Color.DarkGray))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.CardGiftcard, "Free", tint = GreenGift, modifier = Modifier.size(18.dp))
            }
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.MoreHoriz, "More", tint = TextGrey)
        }
    }
}