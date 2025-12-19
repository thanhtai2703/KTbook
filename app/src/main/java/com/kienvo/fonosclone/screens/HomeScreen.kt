package com.kienvo.fonosclone.screens

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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.kienvo.fonosclone.model.getBooks
import com.kienvo.fonosclone.model.getHomeScreenData
import com.kienvo.fonosclone.ui.theme.DarkBg
import com.kienvo.fonosclone.ui.theme.PaleYellow
import com.kienvo.fonosclone.ui.theme.PaleYellowDark
import com.kienvo.fonosclone.widgets.BookSection
import com.kienvo.fonosclone.widgets.FonosCarousel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavController? = null,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    // Lấy danh sách các danh mục sách (Data động)
    val homeCategories = remember { getHomeScreenData() }

    // Lấy list sách riêng để hiển thị Carousel (Banner)
    val carouselBooks = remember { getBooks() }

    // State quản lý hình nền thay đổi theo carousel
    val (currentBgUrl, setCurrentBgUrl) = remember { mutableStateOf(carouselBooks.firstOrNull()?.coverUrl) }

    // Firebase Authentication - Kiểm tra trạng thái đăng nhập thực tế
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var isLoggedIn by remember { mutableStateOf(currentUser != null) }
    val userAvatarUrl = currentUser?.photoUrl?.toString()
        ?: "https://icons.veryicon.com/png/o/miscellaneous/common-icons-31/default-avatar-2.png"
    val userEmail = currentUser?.email ?: ""

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
                            "Fonos",
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
                                    // Click vào avatar để test đăng xuất
                                    if (isLoggedIn) {
                                        auth.signOut()
                                        isLoggedIn = false
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
