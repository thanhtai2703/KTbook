package com.kienvo.fonosclone.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
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

// --- DATA MODEL ---
data class EbookData(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val rating: Double,
    val rank: Int = 0
)

// --- COLORS ---
// Nền tổng thể Nâu Cam
private val BgTop = Color(0xFF5D3A29)
private val BgMid = Color(0xFF3E2723)
private val BgBottom = Color(0xFF111218)

// Gradient Tím cho TopBar khi cuộn
private val PurpleBarStart = Color(0xFF422A68)
private val PurpleBarEnd = Color(0xFF24135F)

private val BookCardColor = Color(0xFF1E2027)
private val BorderThin = Color.White.copy(alpha = 0.10f)
private val SubText = Color(0xFFB6BAC3)
private val InfinityPink = Color(0xFFD63D8B)

@Composable
fun EbookScreen(navController: NavController) {
    // Quản lý trạng thái cuộn của danh sách
    val listState = rememberLazyListState()

    // Tính toán xem đã cuộn qua 1 khoảng nhất định chưa
    val showStickyHeader by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Nâu Cam ấm áp
        EbookBackground()

        // 2. Nội dung chính
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 80.dp, bottom = 100.dp) // Top padding để tránh TopBar đè nội dung
        ) {
            item{
                Spacer(modifier = Modifier.height(16.dp))
            }
            // Banner
            item {
                EbookBanner()
                Spacer(Modifier.height(24.dp))
            }

            // --- SECTION 1: TOP 10 (Sử dụng Card tràn viền, bỏ Rank) ---
            item {
                SectionHeader("Top 10 Thịnh Hành Hôm Nay 🔥", onClick = {})
                Spacer(Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(getTopEbooks()) { book ->
                        AppEbookCard(book)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            // --- CÁC SECTION KHÁC ---
            item { EbookSection(title = "Ebook Miễn Phí", books = getFreeEbooks()) }
            item { EbookSection(title = "Văn Học Kinh Điển", books = getLiteratureBooks()) }
            item { EbookSection(title = "Sức Khỏe & Dinh Dưỡng", books = getHealthBooks()) }
            item { EbookSection(title = "Tâm Lý Học Ứng Dụng", books = getPsychologyBooks()) }
            item { EbookSection(title = "Phong Cách Sống", books = getLifestyleBooks()) }
            item { EbookSection(title = "Triết Học & Tư Tưởng", books = getPhilosophyBooks()) }
            item { EbookSection(title = "Kinh Tế & Đầu Tư", books = getBusinessBooks()) }
        }

        // 3. Custom Top Bar (Nằm đè lên trên cùng)
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
            .height(100.dp) // Chiều cao TopBar
            .background(
                brush = if (isVisible) Brush.verticalGradient(
                    colors = listOf(PurpleBarStart, PurpleBarEnd)
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
            // [ĐÃ SỬA] Nút Back: Quay về màn hình "search"
            IconButton(onClick = {
                navController.navigate("search") {
                    popUpTo("ebook") { inclusive = true }
                    launchSingleTop = true
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Search", tint = Color.White)
            }

            // Tiêu đề Ebook (Hiện ra khi cuộn)
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

            // Nút Search
            IconButton(onClick = { navController.navigate("active_search") }) {
                Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.White)
            }
        }
    }
}

@Composable
fun EbookSection(title: String, books: List<EbookData>) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        SectionHeader(title, onClick = {})
        Spacer(Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                AppEbookCard(book)
            }
        }
    }
}

@Composable
private fun EbookBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BgTop, BgMid, BgBottom),
                    startY = 0f, endY = 1800f
                )
            )
            .drawWithCache {
                val glowPurple = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFAB91).copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.1f),
                    radius = size.minDimension * 1.5f
                )
                onDrawBehind { drawRect(glowPurple) }
            }
    )
}

@Composable
private fun EbookBanner() {
    val shape = RoundedCornerShape(18.dp)
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(130.dp),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, BorderThin)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFBF360C), Color(0xFF5D4037)),
                        start = Offset(0f, 0f), end = Offset(1000f, 0f)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💎", fontSize = 32.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Ebook Premium", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Đọc sách không giới hạn với gói\nhội viên Ebook.", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, lineHeight = 18.sp)
                }
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
            tint = SubText,
            modifier = Modifier.size(24.dp).clickable { onClick() }
        )
    }
}

// --- CARD CHUẨN APP (Tràn viền, không rank) ---
// Thay thế hoàn toàn cho các loại card cũ
// --- CARD MỚI: ẢNH DÀI HƠN, PHẦN DƯỚI NHỎ LẠI ---
@Composable
private fun AppEbookCard(book: EbookData) {
    // Kích thước card tổng thể
    val cardWidth = 148.dp
    // [ĐIỀU CHỈNH 1] Tăng tổng chiều cao lên để có không gian cho ảnh dọc
    val cardHeight = 275.dp

    Surface(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight),
        shape = RoundedCornerShape(12.dp),
        color = BookCardColor,
        border = BorderStroke(1.dp, BorderThin)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. PHẦN ẢNH BÌA (Tràn viền ngang)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // [ĐIỀU CHỈNH 2] Tăng chiều cao ảnh lên đáng kể (từ 190dp -> 235dp)
                    // để phù hợp với tỉ lệ bìa sách dọc, giúp ảnh ít bị cắt hơn.
                    .height(235.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    // Vẫn giữ Crop để ảnh lấp đầy khung, nhưng nhờ khung cao hơn nên sẽ hiện trọn vẹn hơn
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray)
                )

                // Badge Vô cực
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(InfinityPink),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "∞",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.offset(y = (-1).dp)
                    )
                }
            }

            // 2. PHẦN THÔNG TIN BÊN DƯỚI (Nhỏ lại)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // [ĐIỀU CHỈNH 3] Giảm vertical padding (từ 10dp xuống 4dp) để phần dưới gọn lại
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center, // Căn giữa nội dung trong không gian hẹp
                horizontalAlignment = Alignment.Start
            ) {
                // Dùng Spacer để đẩy nội dung xuống giữa nếu cần thiết, hoặc để Arrangement.Center tự lo
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${book.rating}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// --- DUMMY DATA GENERATORS ---
fun getTopEbooks() = listOf(
    EbookData("1", "Minh Triết Trong Ăn Uống", "Ngô Đức Vượng", "https://voiz-prod.s3-wewe.cloud.cmctelecom.vn/uploads/avatar/filename/1077/webp_26db9a2ce19ef089fec27418e06920d26706db72.webp", 4.0, 1),
    EbookData("2", "Hiểu Về Trái Tim", "Minh Niệm", "https://dtv-ebook.com.vn/images/files_2/2020/hieu-ve-trai-tim-minh-niem.jpg", 4.9, 2),
    EbookData("3", "Tư Duy Ngược", "Adam Grant", "https://firstnews.vn/upload/products/original/-1729758499.jpg", 4.5, 3),
    EbookData("4", "Đắc Nhân Tâm", "Dale Carnegie", "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg", 4.8, 4)
)

fun getFreeEbooks() = listOf(
    EbookData("5", "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "http://thdaithinha.giaoducmelinh.edu.vn/upload/28687/fck/01250404/2024_06_19_11_21_4419.jpg", 4.8),
    EbookData("6", "Số Đỏ", "Vũ Trọng Phụng", "https://product.hstatic.net/200000017360/product/bia_sodo3-b1_b32d805ef78846fab8d0d6c1c7fc887b_master.jpg", 4.7),
    EbookData("7", "Tắt Đèn", "Ngô Tất Tố", "https://dilib.vn/img/news/2022/11/larger/7820-tat-den-1.jpg?v=1370", 4.6),
)

fun getLiteratureBooks() = listOf(
    EbookData("8", "Rừng Na Uy", "Haruki Murakami", "https://cdn1.fahasa.com/media/catalog/product/8/9/8936024919047_1_1.jpg", 4.5),
    EbookData("9", "Trăm Năm Cô Đơn", "G.G. Marquez", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/tramnamcodon01-0ce89a31-455d-4492-b0ce-1f6329222273.jpg?v=1705552510463", 4.8),
    EbookData("10", "Chí Phèo", "Nam Cao", "https://book.sachgiai.com/uploads/book/truyen-ngan-chi-pheo/truyen-ngan-chi-pheo-nam-cao.jpg", 4.9),
)

fun getHealthBooks() = listOf(
    EbookData("11", "Nhân Tố Enzyme", "Hiromi Shinya", "https://product.hstatic.net/200000900535/product/1876029511_bia_nhan-to-enzyme-2_f659d7186380445db4644d05997e448f_1024x1024.jpg", 4.4),
    EbookData("12", "Cơ Thể 4 Giờ", "Tim Ferriss", "https://minhkhai.com.vn/hinhlon/8935246915936.jpg", 4.3),
)

fun getPsychologyBooks() = listOf(
    EbookData("13", "Tư Duy Nhanh & Chậm", "Daniel Kahneman", "https://bizweb.dktcdn.net/thumb/1024x1024/100/197/269/products/462558750-1083111936819329-1957541486232979466-n.png?v=1730363480047", 4.7),
    EbookData("14", "Phi Lý Trí", "Dan Ariely", "https://bizweb.dktcdn.net/thumb/1024x1024/100/197/269/products/11.png?v=1676087122193", 4.6),
)

fun getLifestyleBooks() = listOf(
    EbookData("15", "Lối Sống Tối Giản", "Sasaki Fumio", "https://product.hstatic.net/200000900535/product/bia_loi-song-toi-gian-01-1-2_647828c3e1dc4b0182e4333e49ebe6f6.jpg", 4.2),
    EbookData("16", "Lagom", "Nhiều tác giả", "https://cdn1.fahasa.com/media/catalog/product/8/9/8934974184706.jpg", 4.1),
)

fun getPhilosophyBooks() = listOf(
    EbookData("17", "Sự An Ủi Của Triết Học", "Alain de Botton", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/suanuitriethoc01-8b3e5c36-50b5-4eb4-8c04-9b17c21b31c2.jpg?v=1736215302887", 4.5),
    EbookData("18", "Thế Giới Của Sophie", "Jostein Gaarder", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/thegioicuasophie01.jpg?v=1710306286017", 4.8),
)

fun getBusinessBooks() = listOf(
    EbookData("19", "Cha Giàu Cha Nghèo", "Robert Kiyosaki", "https://bookfun.vn/wp-content/uploads/2024/10/cha-giau-cha-ngheo.jpg", 4.9),
    EbookData("20", "Nhà Đầu Tư Thông Minh", "Benjamin Graham", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/nhadaututhongminhscaled.jpg?v=1705552576643", 4.8),
)