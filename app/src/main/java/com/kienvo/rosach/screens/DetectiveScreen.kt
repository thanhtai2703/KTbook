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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.viewmodel.BookViewModel

// Màu sắc theme cho Detective
val NoirDark = Color(0xFF0F0F13)
val NoirRed = Color(0xFF8D2D0F)
val NoirGold = Color(0xFFD4AF37)
val DetectiveTextGrey = Color(0xFFCFCFCF)
val DetectiveStarYellow = Color(0xFFFFC107)

@Composable
fun DetectiveScreen(
    navController: NavController,
    bookViewModel: BookViewModel = viewModel()
) {
    var isGridView by remember { mutableStateOf(false) }

    // Collect books from ViewModel
    val books by bookViewModel.allBooks.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    
    // Filter books locally for 'detective' category
    val detectiveBooks = remember(books) {
        books.filter { it.id.startsWith("detective_") }
    }

    // Load books if list is empty
    LaunchedEffect(Unit) {
        if (books.isEmpty()) {
            bookViewModel.loadAllBooks()
        }
    }

    // URL ảnh nền tạo không khí trinh thám
    val bgImage = "https://media.istockphoto.com/id/1445020821/vi/anh/3d-render-noir-minh-h%E1%BB%8Da-c%E1%BB%A7a-th%C3%A1m-t%E1%BB%AD-toon-v%E1%BB%9Bi-s%C3%BAng-tr%C3%AAn-n%E1%BB%81n-ph%C3%B2ng-t%E1%BB%91i.jpg?s=2048x2048&w=is&k=20&c=r81WB4cOqZrML79mV3mKJAphtCcH5jPWvjQxidzF9t4="

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(bgImage).crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(4.dp)
        )

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // HEADER
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
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFF1B1C20).copy(alpha = 0.95f))
            ) {
                if (isLoading && detectiveBooks.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = NoirGold
                    )
                } else {
                    Column {
                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .width(40.dp).height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .align(Alignment.CenterHorizontally)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Danh sách hồ sơ", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("${detectiveBooks.size} tác phẩm", color = NoirRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

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

                        if (isGridView) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(detectiveBooks) { book ->
                                    DetectiveGridItem(book = book, onClick = {
                                        navController.navigate("detail/${book.id}")
                                    })
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(detectiveBooks) { book ->
                                    DetectiveListItem(book = book, onClick = {
                                        navController.navigate("detail/${book.id}")
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetectiveGridItem(book: Book, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() }.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(150.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(book.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun DetectiveListItem(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF25262C)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(85.dp).fillMaxHeight()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(),
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Text(book.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2)
                Text(book.author, color = DetectiveTextGrey, fontSize = 13.sp, fontStyle = FontStyle.Italic)
            }
        }
    }
}
