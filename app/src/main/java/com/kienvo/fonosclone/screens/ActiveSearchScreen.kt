package com.kienvo.fonosclone.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.fonosclone.model.Book
import kotlinx.coroutines.delay

@Composable
fun ActiveSearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // --- MÀU SẮC ---
    val headerColor = Color(0xFF0F1015)   // Header Fonos
    val contentBoxColor = Color(0xFF13161F) // Nền nội dung

    // --- DỮ LIỆU SÁCH ---
    val allBooks = remember {
        listOf(
            Book("1", "Triều Tuyết Lục", "Coroner's Diary", "https://i.mydramalist.com/R2A0w_4f.jpg"),
            Book("2", "Khó Dỗ Dành", "The First Frost", "https://i.mydramalist.com/BO26w_4f.jpg"),
            Book("18", "Một Thoáng Rực Rỡ", "Ocean Vuong", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/motthoangtarucroonhangian011.jpg?v=1705552591463"),
            Book("20", "Hoàng Tử Bé", "Saint-Exupéry", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/hoangtube.jpg?v=1705552581243"),
            Book("3", "Thần Đèn", "Genie, Make a Wish", "https://images-na.ssl-images-amazon.com/images/I/811PTyrckTL.jpg"),
            Book("4", "Cấm Nguyệt", "Legend of Female", "https://nld.mediacdn.vn/2021/1/22/13-cay-cam-ngot-161132379604435791636.jpg"),
            Book("5", "Trò Chơi Con Mực", "Squid Game", "https://bizweb.dktcdn.net/thumb/1024x1024/100/197/269/products/tri-tue-do-thai.jpg?v=1510634413673"),
            Book("7", "Đọc Vị Bất Kì Ai", "David J. Lieberman", "https://cdn.hstatic.net/products/200000900535/doc_vi_bat_ky_ai_de_khong_bi_loi_dung_-bia_1__tb_2025__899034494358448295b41a80dc16019e.jpg"),
            Book("8", "Muôn Kiếp Nhân Sinh", "Nguyên Phong", "https://product.hstatic.net/200000122283/product/bia1-muonkiepnhansinh3-01_d1a246c6abfd4621bed63b8ca3b73ba9_master.jpg"),
            Book("2", "Đắc Nhân Tâm", "Dale Carnegie", "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg")
        )
    }

    val displayBooks = remember(query) {
        if (query.isBlank()) allBooks else allBooks.filter {
            it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        containerColor = headerColor,
        topBar = {
            // HEADER (GIỮ NGUYÊN)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor)
                    .statusBarsPadding()
                    .padding(top = 28.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) Text("Tìm tên sách, tác giả...", color = Color.Gray, fontSize = 14.sp)
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
                            cursorBrush = SolidColor(headerColor),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                        )
                    }
                    if (query.isNotEmpty()) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(20.dp).clickable { query = "" })
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Hủy", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(contentBoxColor)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(3) }) {
                        Text(
                            text = if (query.isEmpty()) "Được tìm kiếm nhiều" else "Kết quả tìm kiếm",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                        )
                    }

                    // [ANIMATION LOOP] Duyệt qua danh sách và áp dụng animation cho từng item
                    itemsIndexed(displayBooks) { index, book ->
                        AnimatedGridItem(index = index) {
                            GridItem(book)
                        }
                    }

                    item(span = { GridItemSpan(3) }) {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

// --- ANIMATION WRAPPER ---
@Composable
fun AnimatedGridItem(
    index: Int,
    content: @Composable () -> Unit
) {
    // 1. Alpha: Từ mờ (0f) -> Rõ (1f)
    val alphaAnim = remember { Animatable(0f) }
    // 2. TransY: Từ dưới (50px) -> Lên vị trí gốc (0px)
    val yAnim = remember { Animatable(50f) }

    LaunchedEffect(Unit) {
        // Delay tăng dần theo index để tạo hiệu ứng "lần lượt"
        // Index càng lớn thì delay càng lâu
        delay(index * 50L)

        // Chạy animation song song
        alphaAnim.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        yAnim.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier.graphicsLayer {
            alpha = alphaAnim.value
            translationY = yAnim.value
        }
    ) {
        content()
    }
}

// --- ITEM PHONG CÁCH FONOS (Ảnh + Rating + Tên) ---
@Composable
fun GridItem(book: Book) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable { }
    ) {
        // Ảnh Bìa (Sạch, không tag)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(book.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tên sách
        Text(
            text = book.title,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold, // Đậm hơn cho rõ
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 18.sp
        )

        // Tác giả
        Text(
            text = book.author,
            color = Color.Gray,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Rating + Hot Icon (Chuẩn Fonos)
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Sao vàng
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("4.8", color = Color.LightGray, fontSize = 11.sp)

            Spacer(modifier = Modifier.weight(1f))

            // Icon Lửa (Hot)
            Box(
                modifier = Modifier
                    .background(Color(0xFFFF5722).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(2.dp)
            ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(10.dp))
            }
        }
    }
}