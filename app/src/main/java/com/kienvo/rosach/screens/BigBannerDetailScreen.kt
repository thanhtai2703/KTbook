package com.kienvo.rosach.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

// Import Data
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.model.getBooks
import com.kienvo.rosach.model.getHealingBooks
import com.kienvo.rosach.model.getDetectiveBooks

data class BookRanked(
    val rank: Int,
    val coverUrl: String,
    val rating: Double,
    val reviewCount: Int
)

/** ==== COLORS PALETTE ==== **/
private val BgTop = Color(0xFF422A68)
private val BgMid = Color(0xFF2F2349)
private val BgBottom = Color(0xFF111218)
private val BannerCard = Color(0xFF2A2F37)
private val BookCard = Color(0xFF1E2027)
private val SubText = Color(0xFFB6BAC3)
private val SubText2 = Color(0xFF8C909A)
private val InfinityPink = Color(0xFFD63D8B)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BigBannerDetailScreen(navController: NavController) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()

    val showTopBarTitle by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 300
        }
    }

    // --- DATA SETUP ---
    val topBooks = remember {
        listOf(
            BookRanked(1, "https://images-na.ssl-images-amazon.com/images/I/811PTyrckTL.jpg", 5.0, 11),
            BookRanked(2, "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Sherlock_Holmes_-_The_Norwood_Mystery_-_cover.jpg/640px-Sherlock_Holmes_-_The_Norwood_Mystery_-_cover.jpg", 5.0, 6),
            BookRanked(3, "https://upload.wikimedia.org/wikipedia/commons/thumb/3/35/Frankenstein_1818_edition_title_page.jpg/640px-Frankenstein_1818_edition_title_page.jpg", 4.5, 9)
        )
    }

    // Văn Học Thế Giới
    val worldBooks = remember { getBooks() }
    val chunkedWorldBooks = remember(worldBooks) { worldBooks.chunked(3) }
    val worldPagerState = rememberPagerState(pageCount = { chunkedWorldBooks.size })

    // Kiến Thức
    val knowledgeBooks = remember { getHealingBooks() + getBooks() }
    val chunkedKnowledgeBooks = remember(knowledgeBooks) { knowledgeBooks.chunked(6) }
    val knowledgePagerState = rememberPagerState(pageCount = { chunkedKnowledgeBooks.size })

    // Trinh Thám
    val detectiveBooks = remember { getDetectiveBooks() }
    val chunkedDetectiveBooks = remember(detectiveBooks) { detectiveBooks.chunked(3) }
    val detectivePagerState = rememberPagerState(pageCount = { chunkedDetectiveBooks.size })

    // Thiếu Nhi
    val childrenBooks = remember { getHealingBooks() + getDetectiveBooks() }
    val chunkedChildrenBooks = remember(childrenBooks) { childrenBooks.chunked(6) }
    val childrenPagerState = rememberPagerState(pageCount = { chunkedChildrenBooks.size })


    Box(modifier = Modifier.fillMaxSize()) {
        Background()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                TopAppBar(
                    title = {
                        AnimatedVisibility(visible = showTopBarTitle, enter = fadeIn(), exit = fadeOut()) {
                            Text("Sách Tiếng Anh", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Black
                    ),
                    scrollBehavior = scrollBehavior
                )
            }
        ) { padding ->

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Header + Banner
                item { Spacer(Modifier.height(16.dp)) }
                item { BannerSection() }
                item { Spacer(Modifier.height(32.dp)) }

                // 1. TOP CHART
                item { SectionHeader(title = "Top Sách Tiếng Anh", onClick = {}) }
                item { Spacer(Modifier.height(16.dp)) }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(topBooks) { book -> BookRankedCard(book) }
                    }
                }
                item { Spacer(Modifier.height(40.dp)) }

                // 2. VĂN HỌC THẾ GIỚI (Có hiệu ứng lấp ló - Peeking)
                item { SectionHeader(title = "Văn Học Thế Giới", onClick = {}) }
                item { Spacer(Modifier.height(12.dp)) }
                item {
                    HorizontalPager(
                        state = worldPagerState,
                        contentPadding = PaddingValues(start = 16.dp, end = 64.dp),
                        pageSpacing = 16.dp,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) { pageIndex ->
                        val booksInPage = chunkedWorldBooks.getOrNull(pageIndex) ?: emptyList()
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            booksInPage.forEach { book -> HorizontalBookCard(book) }
                        }
                    }
                }
                item { Spacer(Modifier.height(40.dp)) }

                // 3. KIẾN THỨC (Grid 2 hàng, Paging "khựng lại")
                item { SectionHeader(title = "Kiến Thức", onClick = {}) }
                item { Spacer(Modifier.height(12.dp)) }
                item {
                    HorizontalPager(
                        state = knowledgePagerState,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        pageSpacing = 12.dp,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) { pageIndex ->
                        val booksInPage = chunkedKnowledgeBooks.getOrNull(pageIndex) ?: emptyList()
                        val row1 = booksInPage.take(3)
                        val row2 = booksInPage.drop(3).take(3)

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (row1.isNotEmpty()) Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { row1.forEach { VerticalBookCard(it) } }
                            if (row2.isNotEmpty()) Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { row2.forEach { VerticalBookCard(it) } }
                        }
                    }
                }
                item { Spacer(Modifier.height(40.dp)) }

                // 4. TRINH THÁM (Giống Văn Học Thế Giới -> Có lấp ló)
                item { SectionHeader(title = "Trinh Thám", onClick = {}) }
                item { Spacer(Modifier.height(12.dp)) }
                item {
                    HorizontalPager(
                        state = detectivePagerState,
                        contentPadding = PaddingValues(start = 16.dp, end = 64.dp),
                        pageSpacing = 16.dp,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) { pageIndex ->
                        val booksInPage = chunkedDetectiveBooks.getOrNull(pageIndex) ?: emptyList()
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            booksInPage.forEach { book -> HorizontalBookCard(book) }
                        }
                    }
                }
                item { Spacer(Modifier.height(40.dp)) }

                // 5. THIẾU NHI (Giống Kiến Thức -> Grid 2 hàng)
                item { SectionHeader(title = "Thiếu Nhi", onClick = {}) }
                item { Spacer(Modifier.height(12.dp)) }
                item {
                    HorizontalPager(
                        state = childrenPagerState,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        pageSpacing = 12.dp,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) { pageIndex ->
                        val booksInPage = chunkedChildrenBooks.getOrNull(pageIndex) ?: emptyList()
                        val row1 = booksInPage.take(3)
                        val row2 = booksInPage.drop(3).take(3)

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (row1.isNotEmpty()) Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { row1.forEach { VerticalBookCard(it) } }
                            if (row2.isNotEmpty()) Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { row2.forEach { VerticalBookCard(it) } }
                        }
                    }
                }

                item { Spacer(Modifier.height(50.dp)) }
            }
        }
    }
}

/** ================= COMPONENTS ================= **/

// 1. CARD TOP CHART (ĐÃ SỬA: Ảnh tràn viền, bỏ số thứ tự)
@Composable
private fun BookRankedCard(book: BookRanked) {
    val cardWidth = 160.dp
    // Tăng chiều cao lên một chút cho cân đối khi ảnh tràn viền
    val cardHeight = 220.dp
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = BookCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = null
    ) {
        // Dùng Box để xếp chồng các lớp lên nhau
        Box(modifier = Modifier.fillMaxSize()) {
            // LỚP 1 (Dưới cùng): Ảnh bìa tràn viền
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // LỚP 2 (Giữa): Gradient tối ở đáy để làm nền cho text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f // Bắt đầu mờ dần từ phía trên một chút
                        )
                    )
            )

            // LỚP 3 (Trên cùng): Thông tin đánh giá ở đáy
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp), // Padding để không sát mép
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating (Sao + Điểm + Số review)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107), // Màu vàng
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = book.rating.toString(),
                        color = Color.White, // Chữ trắng nổi trên nền tối
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "(${book.reviewCount})",
                        color = SubText, // Màu xám sáng
                        fontSize = 13.sp
                    )
                }

                // Icon Vô cực
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(InfinityPink, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// 2. CARD NGANG (Giữ nguyên - Không badge Level)
@Composable
private fun HorizontalBookCard(book: Book) {
    val randomRating = 4.8
    val randomReview = (10..99).random()
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(70.dp).height(100.dp).clip(RoundedCornerShape(6.dp))) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Text(text = book.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = book.author, color = SubText, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$randomRating ($randomReview)", color = SubText2, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = InfinityPink, modifier = Modifier.size(16.dp))
                Text(text = "∞", color = InfinityPink, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.offset(x = (-2).dp, y = (-1).dp))
            }
        }
        IconButton(onClick = { }) { Icon(Icons.Filled.MoreHoriz, contentDescription = null, tint = SubText2) }
    }
}

// 3. CARD ĐỨNG NHỎ (Giữ nguyên - Không badge Level)
@Composable
private fun VerticalBookCard(book: Book) {
    val cardWidth = 105.dp
    val cardHeight = 165.dp
    val cardShape = RoundedCornerShape(12.dp)
    val randomRating = 4.9
    val randomReview = (5..20).random()
    Card(modifier = Modifier.width(cardWidth).height(cardHeight), shape = cardShape, colors = CardDefaults.cardColors(containerColor = BookCard), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(0.9f)), startY = 250f)))
            Row(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = "$randomRating ($randomReview)", color = Color(0xFFB6BAC3), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
                Box(modifier = Modifier.size(20.dp).background(InfinityPink, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                    Text("∞", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.offset(y = (-1).dp))
                }
            }
        }
    }
}

// ... (Các hàm SectionHeader, Background, BannerSection giữ nguyên như cũ)
@Composable
private fun SectionHeader(title: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = onClick) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = SubText2) }
    }
}

@Composable
private fun Background() {
    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(BgTop, BgMid, BgBottom), startY = 0f, endY = 1500f))
        .drawWithCache {
            val glowPurple = Brush.radialGradient(colors = listOf(Color(0xFF7A5CFF).copy(0.25f), Color.Transparent), center = Offset(size.width * 0.52f, size.height * 0.12f), radius = size.minDimension * 1.45f)
            val glowDeep = Brush.radialGradient(colors = listOf(Color(0xFF4B2D7A).copy(0.22f), Color.Transparent), center = Offset(size.width * 0.22f, size.height * 0.18f), radius = size.minDimension * 1.10f)
            val glowPink = Brush.radialGradient(colors = listOf(InfinityPink.copy(0.10f), Color.Transparent), center = Offset(size.width * 0.88f, size.height * 0.22f), radius = size.minDimension * 1.05f)
            val vignette = Brush.radialGradient(colors = listOf(Color.Transparent, Color.Black.copy(0.45f)), center = Offset(size.width * 0.5f, size.height * 0.95f), radius = size.minDimension * 1.25f)
            onDrawBehind { drawRect(glowPurple); drawRect(glowDeep); drawRect(glowPink); drawRect(vignette) }
        }
    )
}

@Composable
private fun BannerSection() {
    Surface(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(120.dp), shape = RoundedCornerShape(18.dp), color = BannerCard.copy(alpha = 0.6f), border = BorderStroke(1.dp, Color.White.copy(0.1f))) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(54.dp), contentAlignment = Alignment.Center) { Text(text = "🇬🇧", fontSize = 42.sp) }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Sách Tiếng Anh", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(text = "Luyện tiếng Anh với 160 sách kinh\nđiển từ Oxford University Press", color = SubText, fontSize = 13.sp, lineHeight = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}