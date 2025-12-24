package com.kienvo.rosach.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavController) {
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Trung tâm trợ giúp",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Câu hỏi thường gặp",
                color = Yellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            FAQItem(
                question = "Làm sao để tải sách về máy?",
                answer = "Bạn có thể tải sách bằng cách vào chi tiết sách và nhấn nút \"Tải xuống\". Sách đã tải sẽ xuất hiện trong mục Thư viện."
            )

            FAQItem(
                question = "Tôi có thể nghe offline không?",
                answer = "Có, sau khi tải sách về máy, bạn có thể nghe mà không cần kết nối internet."
            )

            FAQItem(
                question = "Làm sao để đổi mật khẩu?",
                answer = "Vào Cá nhân > Bảo mật > Đổi mật khẩu để thay đổi mật khẩu của bạn."
            )

            FAQItem(
                question = "Ứng dụng hỗ trợ những định dạng nào?",
                answer = "Ứng dụng hỗ trợ MP3, M4A và các định dạng audiobook phổ biến."
            )

            FAQItem(
                question = "Tôi quên mật khẩu, phải làm sao?",
                answer = "Bạn có thể nhấn \"Quên mật khẩu\" ở màn hình đăng nhập để đặt lại mật khẩu qua email."
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                question,
                color = Yellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                answer,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

