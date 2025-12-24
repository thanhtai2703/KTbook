package com.kienvo.rosach.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.data.SampleData
import com.kienvo.rosach.model.Book

// Màu sắc theme cho Detective (Giữ nguyên & Mở rộng)
val DetectiveBackground = Brush.verticalGradient(listOf(
    Color(0xFFBF360C), // Đỏ đậm gốc
    Color(0xFF8D2D0F),
    Color(0xFF5D1F0C)
))

// Palette mở rộng cho UI phức tạp hơn
val NoirDark = Color(0xFF0F0F13)
val NoirRed = Color(0xFF8D2D0F)
val NoirGold = Color(0xFFD4AF37)
val DetectiveTextGrey = Color(0xFFCFCFCF)
val DetectiveStarYellow = Color(0xFFFFC107)
val DetectiveRatingBarBg = Color(0xFF4A2C1F)

@Composable
fun DetectiveScreen(navController: NavController) {
    var isGridView by remember { mutableStateOf(false) }

    val books: List<Book> = remember {
        SampleData.detectiveBooks
    }

    // URL ảnh nền tạo không khí trinh thám (Sương mù/Phố đêm)
    val bgImage = "https://media.istockphoto.com/id/1445020821/vi/anh/3d-render-noir-minh-h%E1%BB%8Da-c%E1%BB%A7a-th%C3%A1m-t%E1%BB%AD-toon-v%E1%BB%9Bi-s%C3%BAng-tr%C3%AAn-n%E1%BB%81n-ph%C3%B2ng-t%E1%BB%91i.jpg?s=2048x2048&w=is&k=20&c=r81WB4cOqZrML79mV3mKJAphtCcH5jPWvjQxidzF9t4="

    Box(modifier = Modifier.fillMaxSize()) {
        // --- LỚP 1: BACKGROUND PHỨC TẠP ---
        // Ảnh nền mờ
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(bgImage).crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(4.dp) // Làm mờ nhẹ để tách biệt nội dung
        )

        // Lớp phủ Gradient tối màu (Scrim) để text dễ đọc
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            NoirRed.copy(alpha = 0.8f),
                            NoirDark
                        )
                    )
                )
        )

        // Họa tiết trang trí (Icon kính lúp khổng lồ mờ ở góc)
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.05f),
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-50).dp)
        )

        // --- LỚP 2: NỘI DUNG CHÍNH ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // HEADER (Được thiết kế lại)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        .size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Tiêu đề nhỏ bên phải (Breadcrumb)
                Text(
                    text = "THỂ LOẠI TRINH THÁM",
                    color = NoirGold.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Sách Trinh Thám",
                    color = Color.White,
                    fontSize = 32.sp, // Font lớn hơn
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.displaySmall,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Khám phá những bí ẩn trong bóng tối.",
                    color = DetectiveTextGrey,
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CONTAINER NỘI DUNG (Sheet bo tròn)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Fill max height còn lại
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFF1B1C20).copy(alpha = 0.95f)) // Nền tối gần như đặc
            ) {
                Column {
                    // Thanh gạt nhỏ trang trí
                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .align(Alignment.CenterHorizontally)
                    )

                    // Section Toolbar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Danh sách hồ sơ",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${books.size} tác phẩm",
                                color = NoirRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Nút chuyển đổi View đẹp hơn
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                        ) {
                            IconButton(onClick = { isGridView = !isGridView }) {
                                Icon(
                                    imageVector = if (isGridView) Icons.AutoMirrored.Filled.List else Icons.Filled.GridView,
                                    contentDescription = "Switch View",
                                    tint = NoirGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Hiển thị Grid hoặc List
                    if (isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(books) { book ->
                                DetectiveGridItem(book = book, onClick = {
                                    navController.navigate("detail/${book.id}")
                                })
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(books) { book ->
                                DetectiveListItem(book = book, onClick = {
                                    navController.navigate("detail/${book.id}")
                                })
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }
}

// --- COMPLEX GRID ITEM ---
@Composable
fun DetectiveGridItem(book: Book, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card bìa sách với hiệu ứng đổ bóng
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Badge loại sách
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(NoirDark.copy(alpha = 0.8f), RoundedCornerShape(bottomEnd = 8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if(book.type == "audiobook") Icons.Default.Bookmark else Icons.Default.Star, // Giả lập icon theo type
                        contentDescription = null,
                        tint = NoirGold,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = book.title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = book.author,
            color = DetectiveTextGrey,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// --- COMPLEX LIST ITEM (Case File Style) ---
@Composable
fun DetectiveListItem(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF25262C)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), // Viền mỏng tinh tế
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Cover Image với hiệu ứng bo góc đẹp
            Card(
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                modifier = Modifier
                    .width(85.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Book Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Tag nhỏ phía trên
                    Text(
                        text = "VỤ ÁN SỐ #${book.id.hashCode().toString().takeLast(4)}",
                        color = NoirRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = book.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.author,
                        color = DetectiveTextGrey,
                        fontSize = 13.sp,
                        maxLines = 1,
                        fontStyle = FontStyle.Italic,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Custom Rating & Info Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Rating Capsule
                    Surface(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, NoirGold.copy(alpha = 0.3f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = DetectiveStarYellow,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", book.rating),
                                color = NoirGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Type Badge
                    Text(
                        text = book.type.uppercase(),
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}