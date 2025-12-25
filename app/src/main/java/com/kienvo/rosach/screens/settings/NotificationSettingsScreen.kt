package com.kienvo.rosach.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(navController: NavController) {
    var pushNotifications by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    var newBookNotifications by remember { mutableStateOf(true) }
    var updateNotifications by remember { mutableStateOf(false) }
    var promotionNotifications by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Thông báo",
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
                "Cài đặt thông báo",
                color = Yellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            NotificationSettingItem(
                title = "Thông báo đẩy",
                description = "Nhận thông báo trên thiết bị",
                checked = pushNotifications,
                onCheckedChange = { pushNotifications = it }
            )

            NotificationSettingItem(
                title = "Thông báo email",
                description = "Nhận thông báo qua email",
                checked = emailNotifications,
                onCheckedChange = { emailNotifications = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Loại thông báo",
                color = Yellow,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            NotificationSettingItem(
                title = "Sách mới",
                description = "Thông báo khi có sách mới",
                checked = newBookNotifications,
                onCheckedChange = { newBookNotifications = it }
            )

            NotificationSettingItem(
                title = "Cập nhật ứng dụng",
                description = "Thông báo phiên bản mới",
                checked = updateNotifications,
                onCheckedChange = { updateNotifications = it }
            )

            NotificationSettingItem(
                title = "Khuyến mãi",
                description = "Nhận thông báo ưu đãi đặc biệt",
                checked = promotionNotifications,
                onCheckedChange = { promotionNotifications = it }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun NotificationSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    description,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Yellow,
                    checkedTrackColor = Yellow.copy(alpha = 0.5f)
                )
            )
        }
    }
}

