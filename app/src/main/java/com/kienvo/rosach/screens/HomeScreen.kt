package com.kienvo.rosach.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.kienvo.rosach.model.getBooks
import com.kienvo.rosach.model.getHomeScreenData
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.PaleYellow
import com.kienvo.rosach.ui.theme.PaleYellowDark
import com.kienvo.rosach.viewmodel.BookViewModel
import com.kienvo.rosach.widgets.BookSection
import com.kienvo.rosach.widgets.FonosCarousel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavController? = null,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    bookViewModel: BookViewModel = viewModel()
) {
    // Load data từ Firestore
    val allBooks by bookViewModel.allBooks.collectAsState()
    val categories by bookViewModel.categories.collectAsState()
    val featuredBooks by bookViewModel.featuredBooks.collectAsState()
    val booksByCategory by bookViewModel.booksByCategory.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    val error by bookViewModel.error.collectAsState()

    // Load data khi màn hình được tạo
    LaunchedEffect(Unit) {
        bookViewModel.loadAllBooks()
        bookViewModel.loadAllCategoriesWithBooks()
    }

    // Lấy list sách cho carousel (dùng featured books từ Firestore)
    val carouselBooks = if (featuredBooks.isNotEmpty()) featuredBooks else allBooks.take(10)

    // State quản lý hình nền thay đổi theo carousel - Cập nhật khi carouselBooks thay đổi
    var currentBgUrl by remember { mutableStateOf<String?>(null) }

    // Cập nhật background URL khi carouselBooks có dữ liệu
    LaunchedEffect(carouselBooks) {
        if (currentBgUrl == null && carouselBooks.isNotEmpty()) {
            currentBgUrl = carouselBooks.first().coverUrl
        }
    }

    // Firebase Authentication - Kiểm tra trạng thái đăng nhập thực tế
    val auth = FirebaseAuth.getInstance()
    // Observe auth state so UI updates when user signs in/out
    var currentUser by remember { mutableStateOf(auth.currentUser) }
    var isLoggedIn by remember { mutableStateOf(currentUser != null) }
    // derived properties that update when currentUser changes
    val userAvatarUrl = currentUser?.photoUrl?.toString()
        ?: "https://icons.veryicon.com/png/o/miscellaneous/common-icons-31/default-avatar-2.png"

    // Register an AuthStateListener so Compose updates when auth state changes
    DisposableEffect(auth) {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
            isLoggedIn = currentUser != null
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    val topBarGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0.7f),
            Color.Transparent
        )
    )

    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(PaleYellowDark, PaleYellow)
    )

    // Scaffold có nền đen tuyệt đối
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(topBarGradient),
                title = {
                    Column(modifier = Modifier.padding(start = 20.dp)) {
                        Text(
                            "RoSach",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp
                        )
                        Text(
                            "Audio Book Application",
                            color = Color.LightGray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        // 1. Nút Đăng nhập (Chỉ hiện khi CHƯA đăng nhập)
                        if (!isLoggedIn) {
                            Button(
                                onClick = {
                                    navController?.navigate("auth")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp),
                                shape = RoundedCornerShape(30.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(brush = buttonGradient)
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ){
                                    Text(
                                        text = "Đăng nhập",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(15.dp))
                        }

                        // 2. Avatar User (Luôn hiển thị)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable {
                                    // Nếu đã login -> mở trang profile, nếu chưa -> chuyển đến auth
                                    if (isLoggedIn) {
                                        navController?.navigate("profile")
                                    } else {
                                        navController?.navigate("auth")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoggedIn) {
                                // Nếu đã login -> Load ảnh
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(userAvatarUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "User Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Nếu chưa login -> Icon mặc định
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Avatar",
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                },
                // Quan trọng: Màu trong suốt để nhìn xuyên thấu xuống hình nền bên dưới
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
            )
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hình nền mờ động
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(currentBgUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(80.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
            )

            // Hiển thị loading
            if (isLoading && allBooks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = PaleYellow
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Đang tải sách...", color = Color.White)
                    }
                }
            }

            // Hiển thị error
            error?.let { errorMessage ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(errorMessage, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { bookViewModel.refresh() }) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            // Hiển thị nội dung
            if (!isLoading || allBooks.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Carousel Banner
                    item {
                        FonosCarousel(
                            books = carouselBooks,
                            onBookClick = { bookId ->
                                navController?.navigate("detail/$bookId")
                            },
                            onCurrentPosterChanged = { coverUrl ->
                                currentBgUrl = coverUrl
                            },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Nếu có categories từ Firestore
                    if (categories.isNotEmpty()) {
                        items(categories) { category ->
                            val booksForCategory = booksByCategory[category.slug] ?: emptyList()

                            if (booksForCategory.isNotEmpty()) {
                                BookSection(
                                    title = category.name,
                                    books = booksForCategory,
                                    onBookClick = { bookId ->
                                        navController?.navigate("detail/$bookId")
                                    },
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    } else if (allBooks.isNotEmpty()) {
                        // Fallback: Nếu chưa có categories từ Firestore, tự động chia nhóm
                        // Top Thịnh Hành (10 sách đầu tiên)
                        item {
                            val topBooks = allBooks.take(10)
                            if (topBooks.isNotEmpty()) {
                                BookSection(
                                    title = "Top Thịnh Hành 🔥",
                                    books = topBooks,
                                    onBookClick = { bookId ->
                                        navController?.navigate("detail/$bookId")
                                    },
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // Mới Ra Mắt (sách có rating cao)
                        item {
                            val newBooks = allBooks.sortedByDescending { it.rating }.take(8)
                            if (newBooks.isNotEmpty()) {
                                BookSection(
                                    title = "Mới Ra Mắt ✨",
                                    books = newBooks,
                                    onBookClick = { bookId ->
                                        navController?.navigate("detail/$bookId")
                                    },
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // Được Yêu Thích (rating > 4.5)
                        item {
                            val favoriteBooks = allBooks.filter { it.rating >= 4.5 }.take(8)
                            if (favoriteBooks.isNotEmpty()) {
                                BookSection(
                                    title = "Được Yêu Thích ❤️",
                                    books = favoriteBooks,
                                    onBookClick = { bookId ->
                                        navController?.navigate("detail/$bookId")
                                    },
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // Dành Cho Bạn (random)
                        item {
                            val randomBooks = allBooks.shuffled().take(8)
                            if (randomBooks.isNotEmpty()) {
                                BookSection(
                                    title = "Dành Cho Bạn 🎯",
                                    books = randomBooks,
                                    onBookClick = { bookId ->
                                        navController?.navigate("detail/$bookId")
                                    },
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // Tất cả sách (phần cuối)
                        item {
                            BookSection(
                                title = "Tất Cả Sách 📚",
                                books = allBooks,
                                onBookClick = { bookId ->
                                    navController?.navigate("detail/$bookId")
                                },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    }
                }
            }
        }
    }
}