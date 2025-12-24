package com.kienvo.rosach.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
        // [LỚP 1] BACKGROUND IMAGE
        Image(
            painter = painterResource(id = R.drawable.kids_background_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // [LỚP 2] BỐ CỤC
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // --- HEADER ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = "Truyện thiếu nhi",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // --- LIST ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Subtitle
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Những câu chuyện đẹp và hay cho bé",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                }

                // Section Title
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        "Tất cả truyện thiếu nhi",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // [ĐÃ SỬA] Danh sách truyện với Navigation
                items(stories) { book ->
                    // Bọc KidsStoryCard trong Box để bắt sự kiện click
                    Box(
                        modifier = Modifier.clickable {
                            // Điều hướng sang màn hình KidBookDetailScreen
                            navController.navigate("kid_detail/${book.id}")
                        }
                    ) {
                        KidsStoryCard(book)
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}