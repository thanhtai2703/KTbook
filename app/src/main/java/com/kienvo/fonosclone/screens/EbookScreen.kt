package com.kienvo.fonosclone.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kienvo.fonosclone.data.SampleData
import com.kienvo.fonosclone.ui.theme.AppColors
import com.kienvo.fonosclone.widgets.ebook.EbookBackground
import com.kienvo.fonosclone.widgets.ebook.EbookBanner
import com.kienvo.fonosclone.widgets.ebook.EbookCard

@Composable
fun EbookScreen(navController: NavController) {
    val listState = rememberLazyListState()

    val showStickyHeader by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background
        EbookBackground()

        // 2. Nội dung chính
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 80.dp, bottom = 100.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Banner
            item {
                EbookBanner()
                Spacer(Modifier.height(24.dp))
            }

            // Top 10
            item {
                SectionHeader("Top 10 Thịnh Hành Hôm Nay 🔥", onClick = {})
                Spacer(Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(SampleData.topEbooks) { book ->
                        EbookCard(book)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            // Các section khác
            item { EbookSection(title = "Ebook Miễn Phí", books = SampleData.freeEbooks) }
            item { EbookSection(title = "Văn Học Kinh Điển", books = SampleData.literatureEbooks) }
            item { EbookSection(title = "Sức Khỏe & Dinh Dưỡng", books = SampleData.healthEbooks) }
            item { EbookSection(title = "Tâm Lý Học Ứng Dụng", books = SampleData.psychologyEbooks) }
            item { EbookSection(title = "Phong Cách Sống", books = SampleData.lifestyleEbooks) }
            item { EbookSection(title = "Triết Học & Tư Tưởng", books = SampleData.philosophyEbooks) }
            item { EbookSection(title = "Kinh Tế & Đầu Tư", books = SampleData.businessEbooks) }
        }

        // 3. Custom Top Bar
        EbookTopBar(
            isVisible = showStickyHeader,
            navController = navController
        )
    }
}

// --- COMPONENTS ---

@Composable
fun EbookTopBar(isVisible: Boolean, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = if (isVisible) Brush.verticalGradient(
                    colors = listOf(AppColors.EbookPurpleBarStart, AppColors.EbookPurpleBarEnd)
                ) else Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                navController.navigate("search") {
                    popUpTo("ebook") { inclusive = true }
                    launchSingleTop = true
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Search", tint = Color.White)
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Ebook",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            IconButton(onClick = { navController.navigate("active_search") }) {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
            }
        }
    }
}

@Composable
fun EbookSection(title: String, books: List<SampleData.EbookData>) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        SectionHeader(title, onClick = {})
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                EbookCard(book)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppColors.EbookSubText,
            modifier = Modifier.size(24.dp).clickable { onClick() }
        )
    }
}
