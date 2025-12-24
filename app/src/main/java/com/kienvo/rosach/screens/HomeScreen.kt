package com.kienvo.rosach.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.PaleYellow
import com.kienvo.rosach.ui.theme.PaleYellowDark
import com.kienvo.rosach.viewmodel.BookViewModel
import com.kienvo.rosach.widgets.BookSection
import com.kienvo.rosach.widgets.FonosCarousel
import com.kienvo.rosach.widgets.VerticalBookSection
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavController? = null,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    bookViewModel: BookViewModel = viewModel()
) {
    // Load data
    val allBooks by bookViewModel.allBooks.collectAsState()
    val categories by bookViewModel.categories.collectAsState()
    val featuredBooks by bookViewModel.featuredBooks.collectAsState()
    val booksByCategory by bookViewModel.booksByCategory.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    val error by bookViewModel.error.collectAsState()

    // [MỚI] Lấy các sections đã được tính sẵn từ ViewModel (không shuffle lại)
    val recommendedBooks by bookViewModel.recommendedBooks.collectAsState()
    val topBooks by bookViewModel.topBooks.collectAsState()
    val favoriteBooks by bookViewModel.favoriteBooks.collectAsState()
    val newBooks by bookViewModel.newBooks.collectAsState()
    val kidsBooks by bookViewModel.kidsBooks.collectAsState()
    val trendingBooks by bookViewModel.trendingBooks.collectAsState()

    LaunchedEffect(Unit) {
        bookViewModel.loadAllBooks()
        bookViewModel.loadAllCategoriesWithBooks()
    }

    val carouselBooks = if (featuredBooks.isNotEmpty()) featuredBooks else allBooks.take(10)
    var currentBgUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(carouselBooks) {
        if (currentBgUrl == null && carouselBooks.isNotEmpty()) {
            currentBgUrl = carouselBooks.first().coverUrl.toString()
        }
    }

    // Auth Logic
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var isLoggedIn by remember { mutableStateOf(currentUser != null) }
    val userAvatarUrl = currentUser?.photoUrl?.toString()
        ?: "https://icons.veryicon.com/png/o/miscellaneous/common-icons-31/default-avatar-2.png"

    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
            isLoggedIn = currentUser != null
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    // [DI CHUYỂN HÀM LÊN ĐÂY - TRƯỚC KHI SỬ DỤNG]
    fun navigateToDetailWithBook(book: Book, sourceKey: String) {
        val title = URLEncoder.encode(book.title, StandardCharsets.UTF_8.toString())
        val author = URLEncoder.encode(book.author, StandardCharsets.UTF_8.toString())
        val cover = URLEncoder.encode(book.coverUrl.toString(), StandardCharsets.UTF_8.toString())
        navController?.navigate("detail/${book.id}?title=$title&author=$author&coverUrl=$cover&sourceKey=$sourceKey")
    }

    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(PaleYellowDark, PaleYellow)
    )

    // Gradient Scrim CỐ ĐỊNH (Không cuộn theo list)
    val fixedTopScrim = Brush.verticalGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0.9f), // Đậm hơn chút ở mép trên
            Color.Black.copy(alpha = 0.6f),
            Color.Transparent
        )
    )

    val bottomFadeGradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, DarkBg.copy(alpha = 0.8f), DarkBg)
    )
    val darkOverlayColor = Color.Black.copy(alpha = 0.5f)

    // Dùng Box làm container gốc thay vì Scaffold để kiểm soát layer dễ hơn
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- LAYER 1: NỘI DUNG CUỘN (Background ảnh + List sách) ---
        if (!isLoading || allBooks.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp) // Padding đáy cho content khỏi bị che bởi bottom nav
            ) {
                // Item 1: Ảnh nền + Carousel
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Ảnh nền (Nằm trong LazyColumn -> Sẽ trôi lên khi cuộn)
                        if (currentBgUrl != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(550.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data(currentBgUrl).crossfade(true).build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().blur(50.dp)
                                )
                                Box(modifier = Modifier.fillMaxSize().background(darkOverlayColor))

                                // Gradient đáy để hòa vào nền đen bên dưới
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                        .align(Alignment.BottomCenter)
                                        .background(bottomFadeGradient)
                                )
                            }
                        }

                        // Carousel
                        Column {
                            // Spacer lớn để đẩy Carousel xuống, chừa chỗ trống cho TopBar hiển thị trên background sạch
                            Spacer(modifier = Modifier.height(120.dp))

                            FonosCarousel(
                                books = carouselBooks,
                                onBookClick = { bookId ->
                                    val book = carouselBooks.firstOrNull { it.id == bookId }
                                    if (book != null) navigateToDetailWithBook(book, "carousel")
                                    else navController?.navigate("detail/$bookId")
                                },
                                onCurrentPosterChanged = { coverUrl -> currentBgUrl = coverUrl },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // Các mục sách khác (Categories...)
                if (categories.isNotEmpty()) {
                    items(categories) { category ->
                        val booksForCategory = booksByCategory[category.slug] ?: emptyList()
                        if (booksForCategory.isNotEmpty()) {
                            BookSection(
                                title = category.name,
                                books = booksForCategory,
                                onBookClick = { bookId ->
                                    val book = booksForCategory.firstOrNull { it.id == bookId }
                                    if (book != null) navigateToDetailWithBook(book, "category-${category.slug}")
                                    else navController?.navigate("detail/$bookId")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sourceKey = "category-${category.slug}" // Key unique cho mỗi category
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                } else if (allBooks.isNotEmpty()) {
                    // ===== FALLBACK: CÁC SECTION CỐ ĐỊNH =====

                    // 1. ĐƯỢC ĐỀ XUẤT (Sử dụng từ ViewModel)
                    item {
                        if (recommendedBooks.isNotEmpty()) {
                            BookSection(
                                title = "Được Đề Xuất Cho Bạn 🎯",
                                books = recommendedBooks,
                                onBookClick = { id ->
                                    val book = recommendedBooks.firstOrNull { it.id == id }
                                    if (book != null) navigateToDetailWithBook(book, "recommended")
                                    else navController?.navigate("detail/$id")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sourceKey = "recommended"
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // 2. TOP THỊNH HÀNH (Sử dụng từ ViewModel)
                    item {
                        if (topBooks.isNotEmpty()) {
                            BookSection(
                                title = "Top Thịnh Hành 🔥",
                                books = topBooks,
                                onBookClick = { id ->
                                    val book = topBooks.firstOrNull { it.id == id }
                                    if (book != null) navigateToDetailWithBook(book, "top")
                                    else navController?.navigate("detail/$id")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sourceKey = "top"
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // 3. SÁCH YÊU THÍCH (Sử dụng từ ViewModel)
                    item {
                        if (favoriteBooks.isNotEmpty()) {
                            BookSection(
                                title = "Sách Yêu Thích ❤️",
                                books = favoriteBooks,
                                onBookClick = { id ->
                                    val book = favoriteBooks.firstOrNull { it.id == id }
                                    if (book != null) navigateToDetailWithBook(book, "favorite")
                                    else navController?.navigate("detail/$id")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sourceKey = "favorite"
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // 4. MỚI RA MẮT (Sử dụng từ ViewModel)
                    item {
                        if (newBooks.isNotEmpty()) {
                            BookSection(
                                title = "Mới Ra Mắt ✨",
                                books = newBooks,
                                onBookClick = { id ->
                                    val book = newBooks.firstOrNull { it.id == id }
                                    if (book != null) navigateToDetailWithBook(book, "new")
                                    else navController?.navigate("detail/$id")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sourceKey = "new"
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // 5. SÁCH THIẾU NHI (Sử dụng từ ViewModel)
                    item {
                        if (kidsBooks.isNotEmpty()) {
                            BookSection(
                                title = "Sách Thiếu Nhi 👶",
                                books = kidsBooks,
                                onBookClick = { id ->
                                    val book = kidsBooks.firstOrNull { it.id == id }
                                    if (book != null) navigateToDetailWithBook(book, "kids")
                                    else navController?.navigate("detail/$id")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sourceKey = "kids"
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // 6. TRENDING (Sử dụng từ ViewModel)
                    item {
                        if (trendingBooks.isNotEmpty()) {
                            BookSection(
                                title = "Đang Hot 📈",
                                books = trendingBooks,
                                onBookClick = { id ->
                                    val book = trendingBooks.firstOrNull { it.id == id }
                                    if (book != null) navigateToDetailWithBook(book, "trending")
                                    else navController?.navigate("detail/$id")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sourceKey = "trending"
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    // 7. TẤT CẢ SÁCH (Hiển thị theo chiều dọc)
                    item {
                        if (allBooks.isNotEmpty()) {
                            VerticalBookSection(
                                title = "Tất Cả Sách 📚",
                                books = allBooks,
                                onBookClick = { id ->
                                    val book = allBooks.firstOrNull { it.id == id }
                                    if (book != null) navigateToDetailWithBook(book, "allBooks")
                                    else navController?.navigate("detail/$id")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
                                sourceKey = "allBooks"
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                // ...existing code for allBooks section...
            }
        }

        // --- LAYER 2: TOP BAR SCRIM (Gradient đen cố định) ---
        // Cái này nằm NGOÀI LazyColumn nên nó ĐỨNG YÊN, không trôi đi đâu cả.
        // Nó đảm bảo vùng trên cùng luôn tối để text trắng luôn nổi.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp) // Chiều cao đủ che vùng status bar + toolbar
                .align(Alignment.TopCenter)
                .background(fixedTopScrim)
        )

        // --- LAYER 3: TOP BAR CONTENT (Logo, Avatar...) ---
        // Nằm trên cùng nhất
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Tránh tai thỏ
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo
            Column {
                Text("RoSach", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Text("Audio Book Application", color = Color.LightGray.copy(alpha = 0.8f), fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }

            // Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isLoggedIn) {
                    Button(
                        onClick = { navController?.navigate("auth") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(brush = buttonGradient)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Đăng nhập", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { if (isLoggedIn) navController?.navigate("profile") else navController?.navigate("auth") },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoggedIn) {
                        AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(userAvatarUrl).crossfade(true).build(), contentDescription = "Avatar", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
                    }
                }
            }
        }

        // Loading & Error (Giữ nguyên)
        if (isLoading && allBooks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(color = PaleYellow)
            }
        }
        error?.let { errorMessage ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage, color = Color.Red)
            }
        }
    } // Đóng Box
} // Đóng HomeScreen
