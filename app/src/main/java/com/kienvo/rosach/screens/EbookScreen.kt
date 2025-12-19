package com.kienvo.rosach.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kienvo.rosach.data.SampleData
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.ui.theme.AppColors
import com.kienvo.rosach.viewmodel.BookViewModel
import com.kienvo.rosach.widgets.ebook.EbookBackground
import com.kienvo.rosach.widgets.ebook.EbookBanner
import com.kienvo.rosach.widgets.ebook.EbookCard

@Composable
fun EbookScreen(
    navController: NavController,
    bookViewModel: BookViewModel = viewModel()
) {
    val listState = rememberLazyListState()

    // Load data từ Firestore
    val allBooks by bookViewModel.allBooks.collectAsState()
    val booksByCategory by bookViewModel.booksByCategory.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()

    // Load data khi màn hình được tạo
    LaunchedEffect(Unit) {
        if (allBooks.isEmpty()) {
            bookViewModel.loadAllBooks()
        }
        // Load các categories cho ebook
        listOf("top", "free", "literature", "health", "psychology", "lifestyle", "philosophy", "business").forEach {
            bookViewModel.loadBooksByCategory(it)
        }
    }

    // Lấy sách theo category
    val topEbooks = booksByCategory["top"] ?: emptyList()
    val freeEbooks = booksByCategory["free"] ?: emptyList()
    val literatureEbooks = booksByCategory["literature"] ?: emptyList()
    val healthEbooks = booksByCategory["health"] ?: emptyList()
    val psychologyEbooks = booksByCategory["psychology"] ?: emptyList()
    val lifestyleEbooks = booksByCategory["lifestyle"] ?: emptyList()
    val philosophyEbooks = booksByCategory["philosophy"] ?: emptyList()
    val businessEbooks = booksByCategory["business"] ?: emptyList()

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

            // Loading indicator
            if (isLoading && allBooks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.EbookPurpleBarStart)
                    }
                }
            }

            // Top 10
            if (topEbooks.isNotEmpty()) {
                item {
                    SectionHeader("Top 10 Thịnh Hành Hôm Nay 🔥", onClick = {})
                    Spacer(Modifier.height(16.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(topEbooks) { book ->
                            EbookCard(book)
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }

            // Các section khác - chỉ hiển thị nếu có dữ liệu
            if (freeEbooks.isNotEmpty()) {
                item { EbookSection(title = "Ebook Miễn Phí", books = freeEbooks) }
            }
            if (literatureEbooks.isNotEmpty()) {
                item { EbookSection(title = "Văn Học Kinh Điển", books = literatureEbooks) }
            }
            if (healthEbooks.isNotEmpty()) {
                item { EbookSection(title = "Sức Khỏe & Dinh Dưỡng", books = healthEbooks) }
            }
            if (psychologyEbooks.isNotEmpty()) {
                item { EbookSection(title = "Tâm Lý Học Ứng Dụng", books = psychologyEbooks) }
            }
            if (lifestyleEbooks.isNotEmpty()) {
                item { EbookSection(title = "Phong Cách Sống", books = lifestyleEbooks) }
            }
            if (philosophyEbooks.isNotEmpty()) {
                item { EbookSection(title = "Triết Học & Tư Tưởng", books = philosophyEbooks) }
            }
            if (businessEbooks.isNotEmpty()) {
                item { EbookSection(title = "Kinh Tế & Đầu Tư", books = businessEbooks) }
            }

            // Nếu không có dữ liệu từ Firestore, fallback về SampleData
            if (!isLoading && allBooks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Không có dữ liệu",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Vui lòng upload data từ màn hình Admin",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
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
fun EbookSection(title: String, books: List<Book>) {
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
