
package com.kienvo.rosach.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.Yellow
import com.kienvo.rosach.viewmodel.UserViewModel
import kotlin.sequences.ifEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val userProfile by userViewModel.userProfile.collectAsState()

    var username by remember { mutableStateOf(userProfile?.username ?: "") }
    var email by remember { mutableStateOf(userProfile?.email ?: "") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    LaunchedEffect(userProfile) {
        username = userProfile?.username ?: ""
        email = userProfile?.email ?: ""
    }
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Thông tin cá nhân",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar with edit button
            Box(
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            userProfile?.avatarUrl?.ifEmpty {
                                "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face"
                            } ?: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face"
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                IconButton(
                    onClick = { /* TODO: Change avatar */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(40.dp)
                        .background(Yellow, CircleShape)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change avatar",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Form fields
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên người dùng") },
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
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Gray,
                    disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                    disabledLabelColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
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
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Giới thiệu bản thân") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Yellow,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Yellow,
                    unfocusedLabelColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = {
                    // TODO: Save profile
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow
                )
            ) {
                Text(
                    "Lưu thay đổi",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}


