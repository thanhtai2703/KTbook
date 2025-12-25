package com.kienvo.rosach.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.viewmodel.BookViewModel
import com.kienvo.rosach.viewmodel.LibraryViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun KidBookDetailScreen(
    navController: NavController,
    bookId: String?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    bookViewModel: BookViewModel = viewModel(),
    libraryViewModel: LibraryViewModel = viewModel()
) {
    // 1. Lấy dữ liệu từ Firestore
    var book by remember { mutableStateOf<Book?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        if (bookId != null) {
            isLoading = true
            book = bookViewModel.getBookById(bookId)
            isLoading = false
        }
    }

    // Fallback nếu không tìm thấy (tránh crash)
    val displayBook = book ?: Book(bookId ?: "id", "Đang tải...", "...", "", "kid", 0.0)

    // Lấy trạng thái yêu thích từ LibraryViewModel
    val favorites by libraryViewModel.favorites.collectAsState()
    val isFavorite = favorites.any { item -> item.id == displayBook.id }

    // Colors
    val KidOrange = Color(0xFFFF7043)
    val GradientPinkOrange = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE57373), Color(0xFFFFB74D))
    )

    Box(modifier = Modifier.fillMaxSize().background(DarkBg)) {

        // --- ẢNH BÌA ---
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(displayBook.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.TopCenter)
        )

        // Gradient đen mờ ở đỉnh
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(0.6f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        )

        // Nút Back
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 8.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // --- BOTTOM SHEET CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(550.dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFF1E1E1E))
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Tên sách
            Text(
                text = displayBook.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata (Giả lập thời lượng vì model Book không có)
            val fakeDuration = (10..30).random() 
            Text(
                text = "Truyện thiếu nhi • $fakeDuration phút",
                color = KidOrange,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            ActionRowItem(icon = Icons.Default.Download, text = "Tải xuống")
            Spacer(modifier = Modifier.height(16.dp))

            // Nút Yêu thích với logic toggle
            KidFavoriteActionRow(
                isFavorite = isFavorite,
                onToggle = {
                    libraryViewModel.toggleFavorite(displayBook)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mô tả (Giả lập)
            Text(
                text = "${displayBook.title} là một câu chuyện tuyệt vời của tác giả ${displayBook.author}. Câu chuyện mang đến những bài học ý nghĩa về cuộc sống, tình bạn và lòng dũng cảm, được kể lại qua giọng đọc truyền cảm dành riêng cho các bé.",
                color = Color.Gray,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Nút Nghe Ngay
            Button(
                onClick = {
                    navController.navigate("kid_audio_player/${displayBook.id}")
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(50)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(GradientPinkOrange, shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Nghe ngay", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ActionRowItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = Color.LightGray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun KidFavoriteActionRow(
    isFavorite: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clickable { onToggle() }.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavorite) Color.Red else Color.LightGray,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = if (isFavorite) "Đã yêu thích" else "Yêu thích",
            color = Color.LightGray,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}