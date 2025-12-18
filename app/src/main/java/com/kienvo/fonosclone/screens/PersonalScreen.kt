package com.kienvo.fonosclone.screens

import androidx.compose.foundation.background
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
import com.kienvo.fonosclone.ui.theme.DarkBg
import com.kienvo.fonosclone.ui.theme.Yellow
import androidx.navigation.NavController
import com.kienvo.fonosclone.viewmodel.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScreen(
    navController: NavController? = null,
    userViewModel: UserViewModel = viewModel()
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

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
                    createdAt = userProfile?.createdAt ?: 0L
                )

                // Settings Sections
                SettingsSection(
                    title = "Tài khoản",
                    items = listOf(
                        SettingItem("Thông tin cá nhân", Icons.Default.Person),
                        SettingItem("Bảo mật", Icons.Default.Security),
                        SettingItem("Thông báo", Icons.Default.Notifications)
                    )
                )

                SettingsSection(
                    title = "Ứng dụng",
                    items = listOf(
                        SettingItem("Chất lượng âm thanh", Icons.Default.AudioFile),
                        SettingItem("Tải xuống", Icons.Default.Download),
                        SettingItem("Giao diện", Icons.Default.Palette),
                        SettingItem("Ngôn ngữ", Icons.Default.Language)
                    )
                )

                SettingsSection(
                    title = "Hỗ trợ",
                    items = listOf(
                        SettingItem("Trung tâm trợ giúp", Icons.Default.Help),
                        SettingItem("Liên hệ", Icons.Default.ContactSupport),
                        SettingItem("Đánh giá ứng dụng", Icons.Default.Star),
                        SettingItem("Về chúng tôi", Icons.Default.Info)
                    )
                )

                // Logout Button
                OutlinedButton(
                    onClick = {
                        userViewModel.logout()
                        navController?.navigate("auth") {
                            popUpTo(0) { inclusive = true }
                        }
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
    createdAt: Long = 0L
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
            // Profile Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        if (avatarUrl.isNotEmpty()) avatarUrl
                        else "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face"
                    )
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

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

            // Edit Button
            IconButton(
                onClick = { }
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
    items: List<SettingItem>
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
                        showDivider = index < items.size - 1
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItemRow(
    item: SettingItem,
    showDivider: Boolean = true
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
