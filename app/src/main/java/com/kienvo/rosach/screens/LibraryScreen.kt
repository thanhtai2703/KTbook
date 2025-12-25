package com.kienvo.rosach.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.viewmodel.LibraryViewModel
import com.kienvo.rosach.viewmodel.SortOption

// Color Palette - Vibe Dark & Modern
private val LibraryBgTop = Color(0xFF1A1625)
private val LibraryBgBottom = Color(0xFF0D0B12)
private val LibraryCardBg = Color(0xFF211D2B)
private val LibraryAccent = Color(0xFF9D4EDD)
private val LibraryAccentLight = Color(0xFFC77DFF)
private val LibraryTextGrey = Color(0xFFB4B4B8)
private val LibraryTabSelected = Color(0xFF9D4EDD)
private val LibraryTabUnselected = Color(0xFF5A5A5F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    libraryViewModel: LibraryViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Đang nghe", "Yêu thích", "Lịch sử", "Tải xuống")

    // Collect states from ViewModel
    val currentlyListening by libraryViewModel.currentlyListening.collectAsState()
    val favorites by libraryViewModel.favorites.collectAsState()
    val history by libraryViewModel.history.collectAsState()
    val downloads by libraryViewModel.downloads.collectAsState()
    val listeningProgress by libraryViewModel.listeningProgress.collectAsState()
    val favoriteSortOption by libraryViewModel.favoriteSortOption.collectAsState()
    val showDownloadManageDialog by libraryViewModel.showDownloadManageDialog.collectAsState()

    // Refresh data when screen opens
    LaunchedEffect(Unit) {
        libraryViewModel.loadLibraryData()
    }

    // State cho dialog sắp xếp
    var showSortDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LibraryBgTop, LibraryBgBottom)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            LibraryTopBar(navController)

            // Tab Row
            LibraryTabRow(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on selected tab
            when (selectedTab) {
                0 -> CurrentlyListeningTab(
                    currentlyListening,
                    listeningProgress,
                    navController,
                    onDelete = { book ->
                        libraryViewModel.removeFromCurrentlyListening(book)
                    }
                )
                1 -> FavoritesTab(
                    favorites,
                    navController,
                    currentSortOption = favoriteSortOption,
                    onSortClick = { showSortDialog = true },
                    onRemoveFavorite = { book ->
                        libraryViewModel.removeFromFavorites(book)
                    }
                )
                2 -> HistoryTab(
                    history,
                    navController,
                    onClearAll = { libraryViewModel.clearAllHistory() },
                    onDelete = { book ->
                        libraryViewModel.removeFromHistory(book)
                    }
                )
                3 -> DownloadsTab(
                    downloads,
                    navController,
                    totalSize = libraryViewModel.getTotalDownloadSize(),
                    onManageClick = { libraryViewModel.showDownloadManageDialog() },
                    onDelete = { book ->
                        libraryViewModel.removeFromDownloads(book)
                    }
                )
            }
        }

        // Sort Dialog
        if (showSortDialog) {
            SortDialog(
                currentOption = favoriteSortOption,
                onDismiss = { showSortDialog = false },
                onSortSelected = { option ->
                    libraryViewModel.sortFavorites(option)
                    showSortDialog = false
                }
            )
        }

        // Download Manage Dialog
        if (showDownloadManageDialog) {
            DownloadManageDialog(
                totalBooks = downloads.size,
                totalSize = libraryViewModel.getTotalDownloadSize(),
                onDismiss = { libraryViewModel.hideDownloadManageDialog() },
                onClearAll = { libraryViewModel.clearAllDownloads() }
            )
        }
    }
}

@Composable
private fun LibraryTopBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Thư viện",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bộ sưu tập của bạn",
                color = LibraryTextGrey,
                fontSize = 14.sp
            )
        }

        // Only search button, removed three-dot menu
        IconButton(
            onClick = { navController.navigate("active_search") },
            modifier = Modifier
                .size(40.dp)
                .background(LibraryCardBg, CircleShape)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun LibraryTabRow(
    tabs: List<String>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        indicator = { },
        divider = { },
        edgePadding = 16.dp
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            val animatedAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.6f,
                animationSpec = tween(300), label = ""
            )

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) LibraryTabSelected.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
            ) {
                Text(
                    text = title,
                    color = if (isSelected) LibraryAccentLight else LibraryTabUnselected,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .alpha(animatedAlpha)
                )
            }
        }
    }
}

// Tab 1: Đang nghe (Currently Listening)
@Composable
private fun CurrentlyListeningTab(
    books: List<Book>,
    progressMap: Map<String, Float>,
    navController: NavController,
    onDelete: (Book) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
    ) {
        if (books.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Headphones,
                    title = "Chưa có sách đang nghe",
                    subtitle = "Bắt đầu nghe một cuốn sách nào đó"
                )
            }
        } else {
            item {
                Text(
                    text = "Tiếp tục nghe",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(books) { book ->
                val progress = progressMap[book.id] ?: 0f
                CurrentlyListeningCard(book, progress, navController, onDelete)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun CurrentlyListeningCard(
    book: Book,
    progress: Float,
    navController: NavController,
    onDelete: (Book) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LibraryCardBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("detail/${book.id}") }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book Cover
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Book Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.author,
                        color = LibraryTextGrey,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress
                    Column {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = LibraryAccent,
                            trackColor = LibraryTabUnselected.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${(progress * 100).toInt()}% hoàn thành",
                            color = LibraryTextGrey,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Play Button
                IconButton(
                    onClick = { navController.navigate("audio_player/${book.id}") },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(LibraryAccent, LibraryAccentLight)
                            ),
                            CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Padding for X button
                Spacer(modifier = Modifier.width(24.dp))
            }

            // X Button - positioned at top right corner
            IconButton(
                onClick = { onDelete(book) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(32.dp)
                    .background(
                        LibraryCardBg.copy(alpha = 0.8f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = LibraryTextGrey,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Tab 2: Yêu thích (Favorites)
@Composable
private fun FavoritesTab(
    books: List<Book>,
    navController: NavController,
    currentSortOption: SortOption,
    onSortClick: () -> Unit,
    onRemoveFavorite: (Book) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
    ) {
        if (books.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.FavoriteBorder,
                    title = "Chưa có sách yêu thích",
                    subtitle = "Thêm sách vào danh sách yêu thích"
                )
            }
        } else {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${books.size} sách",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onSortClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.Sort,
                            contentDescription = null,
                            tint = LibraryTextGrey,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sắp xếp", color = LibraryTextGrey, fontSize = 14.sp)
                    }
                }
            }

            items(books) { book ->
                FavoriteBookCard(book, navController, onRemoveFavorite)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FavoriteBookCard(book: Book, navController: NavController, onRemoveFavorite: (Book) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("detail/${book.id}") }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(end = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    color = LibraryTextGrey,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = LibraryAccent,
                modifier = Modifier.size(24.dp)
            )
        }

        // X Button - positioned at top right corner
        IconButton(
            onClick = { onRemoveFavorite(book) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 12.dp)
                .size(28.dp)
                .background(
                    LibraryBgTop.copy(alpha = 0.6f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = LibraryTextGrey,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Tab 3: Lịch sử (History)
@Composable
private fun HistoryTab(books: List<Book>, navController: NavController, onClearAll: () -> Unit, onDelete: (Book) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
    ) {
        if (books.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.History,
                    title = "Chưa có lịch sử",
                    subtitle = "Lịch sử nghe sách sẽ hiện ở đây"
                )
            }
        } else {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gần đây",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onClearAll) {
                        Text("Xóa tất cả", color = LibraryAccent, fontSize = 14.sp)
                    }
                }
            }

            // Group by date
            item {
                Text(
                    text = "Hôm nay",
                    color = LibraryTextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(books.take(3)) { book ->
                HistoryBookCard(book, navController, onDelete)
            }

            item {
                Text(
                    text = "Hôm qua",
                    color = LibraryTextGrey,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            items(books.drop(3)) { book ->
                HistoryBookCard(book, navController, onDelete)
            }
        }
    }
}

@Composable
private fun HistoryBookCard(book: Book, navController: NavController, onDelete: (Book) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("detail/${book.id}") }
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .padding(end = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = book.author,
                    color = LibraryTextGrey,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "15:30",
                color = LibraryTextGrey,
                fontSize = 12.sp
            )
        }

        // X Button - positioned at top right corner
        IconButton(
            onClick = { onDelete(book) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 2.dp, end = 12.dp)
                .size(28.dp)
                .background(
                    LibraryBgTop.copy(alpha = 0.6f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = LibraryTextGrey,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Tab 4: Tải xuống (Downloads)
@Composable
private fun DownloadsTab(
    books: List<Book>,
    navController: NavController,
    totalSize: String,
    onManageClick: () -> Unit,
    onDelete: (Book) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp)
    ) {
        if (books.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.CloudDownload,
                    title = "Chưa có sách tải xuống",
                    subtitle = "Tải sách để nghe offline"
                )
            }
        } else {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${books.size} sách",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = totalSize,
                            color = LibraryTextGrey,
                            fontSize = 12.sp
                        )
                    }
                    TextButton(onClick = onManageClick) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = LibraryTextGrey,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quản lý", color = LibraryTextGrey, fontSize = 14.sp)
                    }
                }
            }

            items(books) { book ->
                DownloadBookCard(book, navController, onDelete)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DownloadBookCard(book: Book, navController: NavController, onDelete: (Book) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("detail/${book.id}") }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(end = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    color = LibraryTextGrey,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = LibraryAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "42 MB",
                        color = LibraryTextGrey,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // X Button - positioned at top right corner
        IconButton(
            onClick = { onDelete(book) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 12.dp)
                .size(28.dp)
                .background(
                    LibraryBgTop.copy(alpha = 0.6f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Remove",
                tint = LibraryTextGrey,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Empty State Component
@Composable
private fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = LibraryTextGrey.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            color = LibraryTextGrey,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Sort Dialog
@Composable
private fun SortDialog(
    currentOption: SortOption,
    onDismiss: () -> Unit,
    onSortSelected: (SortOption) -> Unit
) {
    val sortOptions = listOf(
        SortOption.RECENTLY_ADDED to "Thêm gần đây",
        SortOption.NAME_ASC to "Tên A-Z",
        SortOption.NAME_DESC to "Tên Z-A",
        SortOption.AUTHOR_ASC to "Tác giả A-Z",
        SortOption.AUTHOR_DESC to "Tác giả Z-A"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = LibraryAccent
                )
            ) {
                Text("Đóng")
            }
        },
        title = {
            Text(
                text = "Sắp xếp yêu thích",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column {
                sortOptions.forEach { (option, label) ->
                    val isSelected = option == currentOption
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSortSelected(option) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) LibraryAccent else Color.White
                        )
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = LibraryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = LibraryCardBg,
        textContentColor = Color.White
    )
}

// Download Management Dialog
@Composable
private fun DownloadManageDialog(
    totalBooks: Int,
    totalSize: String,
    onDismiss: () -> Unit,
    onClearAll: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onClearAll()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LibraryAccent,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xóa tất cả tải xuống")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = LibraryAccent
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Đóng")
            }
        },
        title = {
            Text(
                text = "Quản lý tải xuống",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Số sách đã tải xuống: $totalBooks",
                    fontSize = 16.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dung lượng tổng cộng: $totalSize",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        },
        containerColor = LibraryCardBg,
        textContentColor = Color.White
    )
}
