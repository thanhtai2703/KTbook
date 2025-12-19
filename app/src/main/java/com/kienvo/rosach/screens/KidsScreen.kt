package com.kienvo.rosach.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kienvo.rosach.data.SampleData
import com.kienvo.rosach.widgets.kids.KidsStoryCard
import com.example.rosach.R

@Composable
fun KidsScreen(navController: NavController) {
    val stories = SampleData.kidsStories

    Box(modifier = Modifier.fillMaxSize()) {
        // [LỚP 1] BACKGROUND IMAGE (Giữ nguyên)
        Image(
            painter = painterResource(id = R.drawable.kids_background_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // [LỚP 2] BỐ CỤC CHIA CẮT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // --- PHẦN 1: COMPACT HEADER (Cố định & Nhỏ gọn) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // Khoảng cách nhỏ với nội dung bên dưới
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hàng chứa nút Back
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp) // [THU NHỎ] Giảm chiều cao từ 56dp -> 48dp
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp) // [THU NHỎ] Icon nhỏ lại chút
                        )
                    }
                }

                // Tiêu đề chính
                Text(
                    text = "Truyện thiếu nhi",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp, // [THU NHỎ] Giảm font từ 28sp -> 24sp cho gọn
                    modifier = Modifier.padding(bottom = 4.dp) // Sát lề dưới hơn
                )

                // Đã XÓA Subtitle ở đây
            }

            // --- PHẦN 2: CONTENT CUỘN ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Chiếm toàn bộ phần còn lại
            ) {
                // [MỚI] ITEM 1: Subtitle (Đưa xuống đây để cuộn theo danh sách)
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Những câu chuyện đẹp và hay cho bé",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp) // Cách xa phần danh sách bên dưới
                    )
                }

                // ITEM 2: Tiêu đề Section
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Tất cả truyện thiếu nhi",
                        color = Color.White,
                        fontSize = 18.sp, // Giảm nhẹ font section cho cân đối
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // ITEM 3: Danh sách truyện
                items(stories) { story ->
                    KidsStoryCard(story)
                }

                // Spacer dưới cùng
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}