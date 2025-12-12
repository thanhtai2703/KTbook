package com.kienvo.fonosclone.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

    // Dữ liệu mẫu
    val categories = listOf(
        CategoryItemData(
            "Sách nói", Color(0xFF5D4037), Color(0xFF8D6E63),
            Icons.Default.Headphones,
            "https://images-na.ssl-images-amazon.com/images/I/811PTyrckTL.jpg"
        ),
        CategoryItemData(
            "PodCourse", Color(0xFF263238), Color(0xFF546E7A),
            Icons.Default.VideoLibrary,
            "https://cdn-icons-png.flaticon.com/512/3163/3163473.png"
        ),
        CategoryItemData(
            "Ebook", Color(0xFF1B5E20), Color(0xFF43A047),
            Icons.Default.MenuBook,
            "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/motthoangtarucroonhangian011.jpg?v=1705552591463"
        ),
        CategoryItemData(
            "Tóm Tắt\n Sách", Color(0xFFE65100), Color(0xFFFFA000),
            Icons.Default.Book,
            "https://cdn.hstatic.net/products/200000900535/doc_vi_bat_ky_ai_de_khong_bi_loi_dung_-bia_1__tb_2025__899034494358448295b41a80dc16019e.jpg"
        ),
        CategoryItemData(
            "Thiếu nhi", Color(0xFFB71C1C), Color(0xFFE57373),
            Icons.Default.ChildCare,
            R.drawable.thieunhi_image
        ),
        CategoryItemData(
            "Thiền", Color(0xFF004D40), Color(0xFF009688),
            Icons.Default.SelfImprovement,
            R.drawable.thien_image
        ),
        CategoryItemData(
            "Truyện ngủ\n& Nhạc", Color(0xFF311B92), Color(0xFF673AB7),
            Icons.Default.Nightlight,
            R.drawable.truyenngu_image
        ),
        CategoryItemData(
            "Podcast", Color(0xFF33691E), Color(0xFF689F38),
            Icons.Default.Mic,
            "https://cdn-icons-png.flaticon.com/512/2368/2368447.png"
        )
    )

    val bannerImageSource: Any = "https://cdn-icons-png.flaticon.com/512/2368/2368447.png"

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
                            // [LOGIC MỚI] Xử lý sự kiện click
                            onClick = {
                                when (category.title) {
                                    "Sách nói" -> {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                    "PodCourse", "Podcast", "Truyện ngủ\n& Nhạc" -> {
                                        // Chuyển về Library (PlaceholderScreen)
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

// [HELPER] Tự động chọn Image hoặc AsyncImage
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
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF24135F), Color(0xFF651FFF)),
                    start = Offset(0f, 0f), end = Offset(1000f, 1000f)
                )
            )
            .clickable {navController.navigate("big_banner_detail") }
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
                .width(120.dp).height(170.dp)
                .offset(x = 20.dp, y = 26.dp)
                .rotate(-22f)
                .alpha(0.95f)
        )
    }
}

// --- WIDGET CARD NHỎ [ĐÃ SỬA THÊM onClick] ---
@Composable
fun CategorySmallCard(
    item: CategoryItemData,
    onClick: () -> Unit // Thêm tham số onClick
) {
    Box(
        modifier = Modifier
            .aspectRatio(1.9f)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.verticalGradient(colors = listOf(item.topColor, item.bottomColor)))
            // [QUAN TRỌNG] Gắn sự kiện click vào đây
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