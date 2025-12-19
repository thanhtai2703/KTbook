package com.kienvo.rosach.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border // [1] Import border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rosach.R
import com.kienvo.rosach.navigation.Screen
import com.kienvo.rosach.data.SampleData

// Model
data class CategoryItemData(
    val title: String,
    val topColor: Color,
    val bottomColor: Color,
    val icon: ImageVector,
    val imageSource: Any
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchScreen(navController: NavController) {
    val headerColor = Color(0xFF20272F)
    val bodyColor = Color(0xFF1B1C20)

    // Dữ liệu mẫu (Giữ nguyên)
    // Use centralized SampleData for images/URLs so it's easier to manage
    val categories = listOf(
        CategoryItemData(
            "Sách nói",
            Color(0xFF6D4C41), Color(0xFF8D6E63),
            Icons.Default.Headphones,
            // pick a representative audiobook cover from SampleData
            SampleData.audioBooks.getOrNull(2)?.coverUrl ?: SampleData.audioBooks.first().coverUrl
        ),
        CategoryItemData(
            "Self-Help",
            Color(0xFF37474F), Color(0xFF546E7A),
            Icons.Default.VideoLibrary,
            // use a healing/popular book for self-help visuals
            SampleData.healingBooks.getOrNull(1)?.coverUrl ?: SampleData.healingBooks.first().coverUrl
        ),
        CategoryItemData(
            "Ebook",
            Color(0xFF0D47A1), Color(0xFF1976D2),
            Icons.AutoMirrored.Filled.MenuBook,
            SampleData.topEbooks.getOrNull(0)?.coverUrl ?: SampleData.topEbooks.first().coverUrl
        ),
        CategoryItemData(
            "Trinh thám",
            Color(0xFFBF360C), Color(0xFFFF8A65),
            Icons.Default.Book,
            SampleData.detectiveBooks.getOrNull(0)?.coverUrl ?: SampleData.detectiveBooks.first().coverUrl
        ),
        CategoryItemData(
            "Thiếu nhi",
            Color(0xFFD81B60), Color(0xFFF48FB1),
            Icons.Default.ChildCare,
            // kidsStories uses Any (String or drawable)
            SampleData.kidsStories.getOrNull(0)?.imageUrl ?: R.drawable.kids_image
        ),
        CategoryItemData(
            "Tôn giáo",
            Color(0xFF004D40), Color(0xFF00796B),
            Icons.Default.SelfImprovement,
            // pick the Kinh Thánh entry from audioBooks (index 5)
            SampleData.audioBooks.getOrNull(5)?.coverUrl ?: SampleData.audioBooks.first().coverUrl
        ),
        CategoryItemData(
            "Thiên văn",
            Color(0xFF283593), Color(0xFF5E35B1),
            Icons.Default.Nightlight,
            R.drawable.bedtime_story_image
        ),
        CategoryItemData(
            "Healing",
            Color(0xFF2E7D32), Color(0xFF66BB6A),
            Icons.Default.Mic,
            SampleData.healingBooks.getOrNull(0)?.coverUrl ?: SampleData.healingBooks.first().coverUrl
        )
    )

    val bannerImageSource: Any = "https://as1.ftcdn.net/v2/jpg/03/70/42/66/1000_F_370426690_Pejt9KxjWTHPklsKwripaxr0iA17zupF.jpg"

    Scaffold(containerColor = headerColor) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .statusBarsPadding()
        ) {
            // HEADER
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Khám phá", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(36.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Nút tìm kiếm giả
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White)
                        .clickable {
                            navController.navigate("active_search")
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tìm tên sách, tác giả...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // BODY
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(bodyColor)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 24.dp, 16.dp, 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(2) }) {
                        BigBannerCard(
                            icon = Icons.Default.AutoStories,
                            imageSource = bannerImageSource,
                            navController = navController
                        )
                    }

                    items(categories) { category ->
                        CategorySmallCard(
                            item = category,
                            onClick = {
                                when (category.title) {
                                    "Sách nói" -> {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    "Self-Help" -> {
                                        navController.navigate(Screen.SelfHelp.route)
                                    }
                                    "Ebook" -> {
                                        navController.navigate(Screen.Ebook.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    "Thiếu nhi" -> {
                                        navController.navigate(Screen.Kids.route)
                                    }
                                    "PodCourse", "Podcast", "Truyện ngủ\n& Nhạc" -> {
                                        navController.navigate("library") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            }
                        )
                    }

                    item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

// [HELPER]
@Composable
fun CardImage(source: Any, modifier: Modifier) {
    if (source is Int) {
        Image(
            painter = painterResource(id = source),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(source)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

// --- WIDGET BANNER LỚN ---
@Composable
fun BigBannerCard(icon: ImageVector, imageSource: Any, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .clip(RoundedCornerShape(18.dp)) // [QUAN TRỌNG] Bo góc cho nội dung
            // [THÊM] Viền xám nhạt, bo góc 18dp trùng với clip
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF24135F), Color(0xFF24135F)),
                    start = Offset(0f, 0f), end = Offset(1000f, 1000f)
                )
            )
            .clickable { navController.navigate("big_banner_detail") }
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart).padding(start = 18.dp, top = 18.dp).fillMaxWidth(0.65f)) {
            Text("Mới: Sách Tiếng Anh", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Học tiếng Anh qua sách hay cùng phụ đề song ngữ", color = Color(0xFFD1C4E9), fontSize = 14.sp, lineHeight = 18.sp)
        }

        Icon(
            imageVector = icon, contentDescription = null, tint = Color(0xFFEA80FC),
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 18.dp, bottom = 18.dp).size(30.dp)
        )

        CardImage(
            source = imageSource,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(200.dp).height(170.dp)
                .offset(x = 20.dp, y = 70.dp)
                .rotate(-15f)
                .alpha(0.95f)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}

// --- WIDGET CARD NHỎ ---
@Composable
fun CategorySmallCard(
    item: CategoryItemData,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1.9f)
            .clip(RoundedCornerShape(18.dp)) // [QUAN TRỌNG] Bo góc cho nội dung
            // [THÊM] Viền xám nhạt cho card nhỏ
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(colors = listOf(item.topColor, item.bottomColor)))
            .clickable { onClick() }
    ) {
        CardImage(
            source = item.imageSource,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(90.dp).height(120.dp)
                .offset(x = 15.dp, y = 20.dp)
                .rotate(-8f)
                .alpha(0.95f)
                .clip(RoundedCornerShape(8.dp))
        )

        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Text(text = item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = item.icon, contentDescription = null, tint = Color.White.copy(alpha = 0.18f), modifier = Modifier.size(22.dp))
        }
    }
}