package com.kienvo.fonosclone.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

// Model Book

@Composable
fun ActiveSearchScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Dữ liệu giả lập (Giữ nguyên như cũ)
    // --- MÀU SẮC & GIAO DIỆN CỦA BẠN ---
    val headerColor = Color(0xFF0F1015)
    val contentBoxColor = Color(0xFF13161F) // Màu nền nội dung tối

    // --- DỮ LIỆU SÁCH ---
    val hotBooks = listOf(
        Book(
            "18",
            "Một Thoáng Rực Rỡ",
            "Ocean Vuong",
            "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/motthoangtarucroonhangian011.jpg?v=1705552591463"
        ),
        Book("20", "Hoàng Tử Bé", "Saint-Exupéry", "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/hoangtube.jpg?v=1705552581243"),
        Book("17", "Sách Chữa Lành", "Brianna Wiest", "https://davibooks.vn/stores/uploads/z/z4729024325679_319a5b9666920fe8e785dcf3f0102996__97337_image2_800_big.jpg"),
        Book("24", "Chữa Lành Bản Thân", "Dr. Ahona Guha", "https://product.hstatic.net/200000696663/product/8936225390362_36cd29599252412f84c5647b0aa18f6b_1024x1024.jpg")
    )

    val topSearches = listOf(
        Book("1", "Nhà Giả Kim", "Paulo Coelho", "https://nxbhcm.com.vn/Image/Biasach/nhagiakimTB2020.jpg"),
        Book("3", "Sapiens", "Yuval Noah Harari", "https://images-na.ssl-images-amazon.com/images/I/811PTyrckTL.jpg"),
        Book("7", "Đọc Vị Bất Kì Ai", "David J. Lieberman", "https://cdn.hstatic.net/products/200000900535/doc_vi_bat_ky_ai_de_khong_bi_loi_dung_-bia_1__tb_2025__899034494358448295b41a80dc16019e.jpg"),
        Book("8", "Muôn Kiếp Nhân Sinh", "Nguyên Phong", "https://product.hstatic.net/200000122283/product/bia1-muonkiepnhansinh3-01_d1a246c6abfd4621bed63b8ca3b73ba9_master.jpg"),
        Book("4", "Cây Cam Ngọt Của Tôi", "José Mauro", "https://nld.mediacdn.vn/2021/1/22/13-cay-cam-ngot-161132379604435791636.jpg"),
        Book("2", "Đắc Nhân Tâm", "Dale Carnegie", "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg")
    )

    // [LOGIC MỚI] Gộp tất cả sách lại để tìm kiếm
    val allBooks = remember { hotBooks + topSearches }

    // [LOGIC MỚI] Lọc sách theo từ khóa (query)
    val filteredBooks = remember(query) {
        if (query.isBlank()) emptyList()
        else allBooks.filter {
            it.title.contains(query, ignoreCase = true) || // Tìm theo tên sách
                    it.author.contains(query, ignoreCase = true)   // Hoặc tìm theo tác giả
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
            // HEADER GIỮ NGUYÊN CODE CỦA BẠN
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

                    // Ô nhập liệu
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text("Tìm tên sách, tác giả...", color = Color.Gray, fontSize = 14.sp)
                        }
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

                    // Nút Xóa (X) khi có chữ
                    if (query.isNotEmpty()) {
                        Icon(
                            Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray,
                            modifier = Modifier.size(20.dp).clickable { query = "" }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 2. NÚT HỦY (Màu trắng)
                Text(
                    text = "Hủy", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
        }
    ) { paddingValues ->

        // --- BOX NỘI DUNG BÊN DƯỚI ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Box bo góc trượt lên
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(contentBoxColor) // Nền #13161F
            ) {
                // [THÊM NỘI DUNG VÀO ĐÂY]
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Spacer nhỏ đầu danh sách cho thoáng
                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    if (query.isEmpty()) {
                        // SECTION 1: SÁCH HOT
                        item {
                            Text(
                                "Sách hot mới ra mắt",
                                color = Color.White, // Chữ trắng trên nền tối
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                itemsIndexed(hotBooks) { index, book ->
                                    // Animation trượt lên
                                    AnimatedItem(index = index, startDelay = 0) {
                                        HotBookItem(book)
                                    }
                                }
                            }
                        }

                        // SECTION 2: TÌM KIẾM NHIỀU NHẤT
                        item {
                            Text(
                                "Tìm kiếm nhiều nhất",
                                color = Color.White, // Chữ trắng
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
                            )
                        }

                        itemsIndexed(topSearches) { index, book ->
                            // Animation trượt lên nối tiếp
                            AnimatedItem(index = index, startDelay = 300) {
                                TopSearchItem(book)
                            }
                        }
                    } else {
                        // --- TRƯỜNG HỢP 2: ĐANG NHẬP (Hiện Kết Quả Tìm Kiếm) ---
                        item {
                            Text(
                                "Kết quả cho \"$query\"",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        if (filteredBooks.isEmpty()) {
                            item {
                                Text(
                                    "Không tìm thấy sách nào phù hợp.",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 20.dp)
                                )
                            }
                        } else {
                            itemsIndexed(filteredBooks) { index, book ->
                                // Dùng lại TopSearchItem để hiển thị kết quả
                                TopSearchItem(book)
                            }
                        }
                    }

                    // Spacer cuối cùng để tránh bị che bởi BottomBar
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

// --- LOGIC ANIMATION ---
@Composable
fun AnimatedItem(index: Int, startDelay: Int = 0, content: @Composable () -> Unit) {
    val alphaAnim = remember { Animatable(0f) }
    val yAnim = remember { Animatable(50f) }

    LaunchedEffect(Unit) {
        delay(startDelay + (index * 100L))
        alphaAnim.animateTo(1f, animationSpec = tween(400, easing = FastOutSlowInEasing))
        yAnim.animateTo(0f, animationSpec = tween(400, easing = FastOutSlowInEasing))
    }

    Box(modifier = Modifier.graphicsLayer {
        alpha = alphaAnim.value
        translationY = yAnim.value
    }) { content() }
}

// --- UI ITEMS (Đã chỉnh màu chữ cho nền tối) ---

@Composable
fun HotBookItem(book: Book) {
    Column(modifier = Modifier.width(120.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(170.dp).clip(RoundedCornerShape(8.dp)).background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "4.8", color = Color.LightGray, fontSize = 13.sp) // Màu chữ sáng hơn
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(Color(0xFFFF5722).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(4.dp)
            ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
fun TopSearchItem(book: Book) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.width(60.dp).height(90.dp).clip(RoundedCornerShape(6.dp)).background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = book.title,
                color = Color.White, // Tên sách màu trắng
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = book.author, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("4.5 (120)", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.width(1.dp).height(12.dp).background(Color.Gray))
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFF5722).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(2.dp)
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(12.dp))
                }
            }
        }
        Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = Color.Gray, modifier = Modifier.padding(top = 4.dp).size(24.dp))
    }
}