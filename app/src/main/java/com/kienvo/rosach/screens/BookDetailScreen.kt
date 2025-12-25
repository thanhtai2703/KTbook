package com.kienvo.rosach.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.Yellow
import com.kienvo.rosach.viewmodel.BookViewModel
import com.kienvo.rosach.viewmodel.LibraryViewModel
import com.kienvo.rosach.viewmodel.PlayerViewModel
import com.kienvo.rosach.widgets.ActionCircleButton
import com.kienvo.rosach.widgets.AmbienceBottomSheet
import com.kienvo.rosach.widgets.BookStatItem
import com.kienvo.rosach.widgets.ChapterItem
import com.kienvo.rosach.widgets.InfoRow
import com.kienvo.rosach.widgets.MyDivider
import com.kienvo.rosach.widgets.SectionTitle
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: String?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    initialTitle: String? = null,
    initialAuthor: String? = null,
    initialCoverUrl: String? = null,
    sourceKey: String? = null,
    bookViewModel: BookViewModel = viewModel(),
    libraryViewModel: LibraryViewModel = viewModel(),
    playerViewModel: PlayerViewModel = viewModel()
) {
    // Decode URL-encoded parameters
    val decodedTitle = initialTitle?.let {
        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
    }
    val decodedAuthor = initialAuthor?.let {
        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
    }
    val decodedCoverUrl = initialCoverUrl?.let {
        URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
    }

    // Tạo shared element key duy nhất dựa trên sourceKey
    val sharedElementKey = if (!sourceKey.isNullOrEmpty()) {
        "book-${sourceKey}-${bookId}"
    } else {
        "book-detail-${bookId}"
    }

    // Immediate data from navigation
    var book by remember { mutableStateOf<Book?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val immediateTitle = decodedTitle ?: book?.title ?: ""
    val immediateAuthor = decodedAuthor ?: book?.author ?: ""
    val immediateCover = decodedCoverUrl ?: (book?.coverUrl?.toString() ?: "")

    // Fetch full book by id in background
    LaunchedEffect(bookId) {
        if (bookId != null) {
            isLoading = (immediateTitle.isEmpty() && immediateAuthor.isEmpty() && immediateCover.isEmpty())
            val fetched = bookViewModel.getBookById(bookId)
            book = fetched
            isLoading = false

            // Tự động thêm vào lịch sử khi mở DetailScreen
            fetched?.let { libraryViewModel.addToHistory(it) }
        }
    }

    // Check if book is in favorites
    val isFavorite by libraryViewModel.favorites.collectAsState()
    val isBookFavorite = isFavorite.any { item -> item.id == bookId }

    val bookTitle = if (immediateTitle.isNotEmpty()) immediateTitle else (book?.title ?: "Đang tải...")
    val bookAuthor = if (immediateAuthor.isNotEmpty()) immediateAuthor else (book?.author ?: "")
    val bookCover = if (immediateCover.isNotEmpty()) immediateCover else (book?.coverUrl?.toString() ?: "")

    // Sử dụng dữ liệu thực từ book, fallback sang placeholder nếu chưa có
    val bookDesc = if (book?.description?.isNotEmpty() == true) {
        book?.description!!
    } else {
        "Một cuốn sách hay đang chờ bạn khám phá. Thông tin chi tiết đang được cập nhật..."
    }

    val bookType = book?.type ?: "audiobook"
    val bookRating = book?.rating ?: 0.0

    // Danh sách chương - tạm thời dùng placeholder, sau này sẽ lấy từ database
    val chapters = listOf(
        "Chương 1: Mở đầu",
        "Chương 2: Phát triển",
        "Chương 3: Cao trào",
        "Chương 4: Kết thúc"
    )

    // State
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var rainVolume by remember { mutableFloatStateOf(0f) }
    var fireVolume by remember { mutableFloatStateOf(0f) }
    var cafeVolume by remember { mutableFloatStateOf(0f) }

    // Gradient tối dần xuống dưới để InfoSheet hòa vào nền
    val backgroundOverlay = Brush.verticalGradient(
        colors = listOf(
            Color.Black.copy(alpha = 0.3f), // Trên cùng hơi tối để rõ nút back
            Color.Black.copy(alpha = 0.6f),
            DarkBg // Dưới cùng là màu nền đặc
        )
    )

    // BOX TỔNG (Chứa tất cả layers)
    Box(modifier = Modifier.fillMaxSize()) {

        // LAYER 1: BACKGROUND MỜ TOÀN MÀN HÌNH
        if (bookCover.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(bookCover).crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 50.dp) // Blur vừa phải
            )
            // Lớp phủ Gradient để làm tối nền, giúp chữ dễ đọc hơn
            Box(modifier = Modifier.fillMaxSize().background(backgroundOverlay))
        } else {
            // Fallback nền đen nếu chưa có ảnh
            Box(modifier = Modifier.fillMaxSize().background(DarkBg))
        }

        // LAYER 2: SCAFFOLD TRONG SUỐT (Chứa nội dung + TopBar)
        Scaffold(
            containerColor = Color.Transparent, // QUAN TRỌNG: Để lộ Layer 1
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->

            // Loading View
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.CircularProgressIndicator(color = Yellow)
                }
            }

            // Content Scroll
            if (!isLoading && book != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // BÌA SÁCH (Shared Transition với hiệu ứng nảy)
                    with(sharedTransitionScope) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(bookCover).crossfade(true).build(),
                            contentDescription = "Book Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(180.dp)
                                .height(270.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = sharedElementKey),
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    boundsTransform = { _, _ ->
                                        spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy, // Hiệu ứng nảy vừa phải
                                            stiffness = Spring.StiffnessLow // Mượt mà
                                        )
                                    }
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // TITLE & AUTHOR
                    Text(
                        text = bookTitle,
                        style = MaterialTheme.typography.headlineSmall, // Font nhỏ hơn xíu cho tinh tế
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = bookAuthor,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.7f), // Màu trắng mờ sang hơn Gray
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // ACTIONS BUTTONS
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Favorite Button with toggle functionality
                        IconButton(
                            onClick = {
                                book?.let { libraryViewModel.toggleFavorite(it) }
                            },
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                                .background(
                                    color = if (isBookFavorite) Color(0xFF9D4EDD).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(50)
                                )
                        ) {
                            Icon(
                                imageVector = if (isBookFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isBookFavorite) Color(0xFF9D4EDD) else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Button(
                            onClick = {
                                // Thêm vào Currently Listening khi bấm play
                                book?.let {
                                    libraryViewModel.addToCurrentlyListening(it, progress = 0f)
                                }
                                navController.navigate("audio_player/$bookId")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.height(50.dp).width(160.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Phát Ngay", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        ActionCircleButton(icon = Icons.Default.Tune) { showBottomSheet = true }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // INFO SHEET (Phần đen bo tròn bên dưới)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            // Bo góc tròn trịa nối liền với phần trên
                            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                            // Màu nền của Sheet phải trùng với màu n���n App (DarkBg) hoặc hơi sáng hơn tí xíu
                            .background(DarkBg.copy(alpha = 0.95f))
                            .padding(24.dp)
                    ) {
                        // Handle bar
                        Box(
                            modifier = Modifier
                                .width(40.dp).height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Stats
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            BookStatItem(Icons.Default.AccessTime, "Đang cập nhật", "Thời lượng")
                            BookStatItem(Icons.Default.Category, "Audiobook", "Thể loại")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        MyDivider()
                        Spacer(modifier = Modifier.height(24.dp))

                        // Info Rows
                        InfoRow(label = "Giọng đọc", value = "Đang cập nhật")
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(label = "Nhà xuất bản", value = "Đang cập nhật")

                        Spacer(modifier = Modifier.height(24.dp))
                        MyDivider()
                        Spacer(modifier = Modifier.height(24.dp))

                        // Intro
                        SectionTitle(title = "Giới thiệu")
                        Text(
                            text = bookDesc,
                            color = Color.LightGray,
                            lineHeight = 24.sp,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Justify
                        )

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // BOTTOM SHEET
        if (showBottomSheet) {
            AmbienceBottomSheet(
                sheetState = sheetState,
                onDismiss = { showBottomSheet = false },
                playerViewModel = playerViewModel,
                bookDescription = book?.description ?: ""
            )
        }
    }
}
