package com.kienvo.rosach.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(navController: NavController) {
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Liên hệ",
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
                "Liên hệ với chúng tôi",
                color = Yellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            ContactItem(
                icon = Icons.Default.Email,
                title = "Email",
                content = "support@rosach.vn",
                description = "Gửi email cho chúng tôi"
            )

            ContactItem(
                icon = Icons.Default.Phone,
                title = "Hotline",
                content = "1900-xxxx",
                description = "Hỗ trợ 24/7"
            )

            ContactItem(
                icon = Icons.Default.Web,
                title = "Website",
                content = "www.rosach.vn",
                description = "Truy cập website"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Gửi phản hồi",
                color = Yellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Tiêu đề") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Yellow,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Yellow,
                    unfocusedLabelColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Nội dung") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Yellow,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Yellow,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Button(
                onClick = { /* TODO: Send feedback */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow
                )
            ) {
                Text(
                    "Gửi phản hồi",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ContactItem(
    icon: ImageVector,
    title: String,
    content: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Yellow,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    content,
                    color = Yellow,
                    fontSize = 14.sp
                )
                Text(
                    description,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

