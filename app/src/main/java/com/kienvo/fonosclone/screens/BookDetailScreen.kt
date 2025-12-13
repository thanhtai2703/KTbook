package com.kienvo.fonosclone.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.fonosclone.ui.theme.DarkBg
import com.kienvo.fonosclone.ui.theme.Yellow
import com.kienvo.fonosclone.widgets.ActionCircleButton
import com.kienvo.fonosclone.widgets.AmbienceBottomSheet
import com.kienvo.fonosclone.widgets.AmbienceSliderItem
import com.kienvo.fonosclone.widgets.AudioPlayerWidget
import com.kienvo.fonosclone.widgets.BookStatItem
import com.kienvo.fonosclone.widgets.ChapterItem
import com.kienvo.fonosclone.widgets.InfoRow
import com.kienvo.fonosclone.widgets.MyDivider
import com.kienvo.fonosclone.widgets.SectionTitle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BookDetailScreen(
    navController: NavController,
    bookId: String?,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // Kiểm tra nếu đây là sách "Đắc Nhân Tâm"
    val isDacNhanTam = bookId == "2"

    // Dữ liệu sách thay đổi theo bookId
    val bookTitle = if (isDacNhanTam) "Đắc Nhân Tâm" else "Muôn Kiếp Nhân Sinh"
    val bookAuthor = if (isDacNhanTam) "Dale Carnegie" else "Nguyên Phong"
    val bookCover = if (isDacNhanTam)
        "https://nxbhcm.com.vn/Image/Biasach/dacnhantam86.jpg"
        else "https://product.hstatic.net/200000122283/product/bia1-muonkiepnhansinh3-01_d1a246c6abfd4621bed63b8ca3b73ba9_master.jpg"
    val bookDesc = if (isDacNhanTam)
        "Cuốn sách kinh điển về nghệ thuật giao tiếp và ứng xử. Dale Carnegie chia sẻ những nguyên tắc vàng trong việc xây dựng mối quan hệ, ảnh hưởng tích cực đến người khác và đạt được thành công trong cuộc sống."
        else "Một cuốn sách thức tỉnh về luật nhân quả, luân hồi và vị thế của con người trong vũ trụ..."

    val chapters = if (isDacNhanTam) listOf(
        "Phần 1: Những kỹ thuật cơ bản trong việc ứng xử với con người",
        "Phần 2: Sáu cách để được người khác yêu thích",
        "Phần 3: Làm thế nào để thuyết phục được người khác",
        "Phần 4: Trở thành một nhà lãnh đạo"
    ) else listOf(
        "Chương 1: Cuộc gặp gỡ định mệnh tại New York",
        "Chương 2: Những bí ẩn của tiền kiếp",
        "Chương 3: Luật Nhân Quả vận hành thế nào?",
        "Chương 4: Bài học từ nền văn minh Atlantis",
        "Chương 5: Sự thức tỉnh tâm linh",
        "Chương 6: Lời kết và thông điệp"
    )

    // --- STATE ---
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var rainVolume by remember { mutableFloatStateOf(0f) }
    var fireVolume by remember { mutableFloatStateOf(0f) }
    var cafeVolume by remember { mutableFloatStateOf(0f) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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

        Box(modifier = Modifier.fillMaxSize()) {
            // LAYER 1: BACKGROUND MỜ
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(bookCover).crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 60.dp)
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            // LAYER 2: NỘI DUNG CHÍNH SCROLL
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // --- TOP SECTION: BÌA SÁCH & ACTION ---
                with(sharedTransitionScope) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(bookCover).crossfade(true).build(),
                        contentDescription = "Book Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(200.dp)
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "image-$bookId"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = bookTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tác giả: $bookAuthor",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // CỤM NÚT ACTION
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ActionCircleButton(icon = Icons.Default.FavoriteBorder)
                    Spacer(modifier = Modifier.width(24.dp))
                    Button(
                        onClick = {
                            // Chỉ navigate đến AudioPlayerScreen nếu là sách "Đắc Nhân Tâm"
                            if (isDacNhanTam) {
                                navController.navigate("audio_player/$bookId")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(56.dp)
                            .width(180.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Phát Ngay", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    ActionCircleButton(icon = Icons.Default.Tune) { showBottomSheet = true }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- INFO SHEET (PHẦN BO TRÒN) ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Color(0xFF181818))
                        .padding(24.dp)
                ) {
                    // Thanh nắm (Handle)
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.DarkGray)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. THỐNG KÊ (Icon Row)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        BookStatItem(Icons.Default.AccessTime, if (isDacNhanTam) "8h 30p" else "12h 45p", "Thời lượng")
                        BookStatItem(Icons.Default.Category, if (isDacNhanTam) "Kỹ năng sống" else "Tâm linh", "Thể loại")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    MyDivider()
                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. THÔNG TIN THÊM
                    InfoRow(label = "Giọng đọc", value = if (isDacNhanTam) "Minh Châu" else "Kiên Cặc Bự")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "Nhà xuất bản", value = if (isDacNhanTam) "NXB Tổng Hợp TPHCM" else "First News - Trí Việt")
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(label = "Phát hành", value = "15/08/2024")

                    Spacer(modifier = Modifier.height(24.dp))
                    MyDivider()
                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. GIỚI THIỆU
                    SectionTitle(title = "Giới thiệu")
                    Text(
                        text = bookDesc,
                        color = Color.LightGray,
                        lineHeight = 24.sp,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    MyDivider()
                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. DANH SÁCH CHƯƠNG
                    SectionTitle(title = "Danh sách chương")
                    chapters.forEachIndexed { index, chapterName ->
                        ChapterItem(index = index + 1, name = chapterName)
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // --- BOTTOM SHEET MIXER ---
            if (showBottomSheet) {
                AmbienceBottomSheet(
                    sheetState = sheetState,
                    onDismiss = { showBottomSheet = false },
                    onAiClick = { scope.launch { rainVolume = 0.6f; fireVolume = 0.2f } },
                    rainVol = rainVolume, onRainChange = { rainVolume = it },
                    fireVol = fireVolume, onFireChange = { fireVolume = it },
                    cafeVol = cafeVolume, onCafeChange = { cafeVolume = it }
                )
            }
        }
    }
}
