package com.kienvo.rosach.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.Yellow
import androidx.navigation.NavController
import com.kienvo.rosach.viewmodel.UserViewModel
import com.kienvo.rosach.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScreen(
    navController: NavController? = null,
    userViewModel: UserViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    // Launcher để chọn ảnh từ máy
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { userViewModel.updateAvatar(it) }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cá nhân",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Yellow)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Profile Header
                ProfileHeader(
                    username = userProfile?.username ?: "Người dùng",
                    email = userProfile?.email ?: "",
                    avatarUrl = userProfile?.avatarUrl ?: "",
                    createdAt = userProfile?.createdAt ?: 0L,
                    onEditClick = {
                        // Mở bộ chọn ảnh khi bấm edit
                        launcher.launch("image/*")
                    }
                )

                // Settings Sections
                SettingsSection(
                    title = "Tài khoản",
                    items = listOf(
                        SettingItem("Thông tin cá nhân", Icons.Default.Person),
                        SettingItem("Thông báo", Icons.Default.Notifications)
                    ),
                    navController = navController
                )

                SettingsSection(
                    title = "Hỗ trợ",
                    items = listOf(
                        SettingItem("Trung tâm trợ giúp", Icons.Default.Help),
                        SettingItem("Liên hệ", Icons.Default.ContactSupport),
                        SettingItem("Về chúng tôi", Icons.Default.Info)
                    ),
                    navController = navController
                )

                // Secret Admin Button - Click 5 times to access
                var clickCount by remember { mutableStateOf(0) }
                TextButton(
                    onClick = {
                        clickCount++
                        if (clickCount >= 5) {
                            clickCount = 0
                            navController?.navigate("data_migration")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Version 1.0.0",
                        color = Color.Gray.copy(alpha = 0.3f),
                        fontSize = 12.sp
                    )
                }

                // Logout Button
                OutlinedButton(
                    onClick = {
                        // Gọi logout trên cả 2 ViewModel
                        userViewModel.logout()
                        authViewModel.logout()
                        
                        // Lưu ý: MyApp.kt đã có LaunchedEffect quan sát currentUser
                        // Khi authViewModel.logout() được gọi, currentUser sẽ là null
                        // và app sẽ tự động chuyển về màn hình auth.
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color.Red)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
            }
        }
    }
}

@Composable
fun ProfileHeader(
    username: String = "Người dùng",
    email: String = "",
    avatarUrl: String = "",
    createdAt: Long = 0L,
    onEditClick: () -> Unit = {}
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
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image with Edit overlay
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .clickable { onEditClick() }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            if (avatarUrl.isNotEmpty()) avatarUrl
                            else "https://icons.veryicon.com/png/o/miscellaneous/common-icons-31/default-avatar-2.png"
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Camera Icon overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = username,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (email.isNotEmpty()) {
                    Text(
                        text = email,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                if (createdAt > 0) {
                    val dateText = formatMemberSince(createdAt)
                    Text(
                        text = dateText,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // Edit Button with onClick
            IconButton(
                onClick = onEditClick
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Chỉnh sửa",
                    tint = Yellow
                )
            }
        }
    }
}

// Helper function to format member since date
private fun formatMemberSince(timestamp: Long): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = timestamp
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val year = calendar.get(java.util.Calendar.YEAR)
    return "Thành viên từ tháng $month/$year"
}

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingItem>,
    navController: NavController? = null
) {
    Column {
        Text(
            text = title,
            color = Yellow,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.3f)
            )
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingsItemRow(
                        item = item,
                        showDivider = index < items.size - 1,
                        onClick = {
                            // Handle item click navigation
                            when (item.title) {
                                "Thông tin cá nhân" -> navController?.navigate("settings/profile_info")
                                "Thông báo" -> navController?.navigate("settings/notification")
                                "Giao diện" -> navController?.navigate("settings/theme")
                                "Ngôn ngữ" -> navController?.navigate("settings/language")
                                "Trung tâm trợ giúp" -> navController?.navigate("settings/help_center")
                                "Liên hệ" -> navController?.navigate("settings/contact")
                                "Về chúng tôi" -> navController?.navigate("settings/about")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItemRow(
    item: SettingItem,
    showDivider: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.title,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        if (showDivider) {
            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.2f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

data class SettingItem(
    val title: String,
    val icon: ImageVector
)

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun PersonalScreenPreview() {
    PersonalScreen()
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ProfileHeaderPreview() {
    ProfileHeader()
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun SettingsSectionPreview() {
    SettingsSection(
        title = "Tài khoản",
        items = listOf(
            SettingItem("Thông tin cá nhân", Icons.Default.Person),
            SettingItem("Bảo mật", Icons.Default.Security),
            SettingItem("Thông báo", Icons.Default.Notifications)
        )
    )
}
