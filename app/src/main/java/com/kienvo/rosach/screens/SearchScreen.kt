package com.kienvo.rosach.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rosach.R
import com.kienvo.rosach.navigation.Screen
import com.kienvo.rosach.viewmodel.SearchViewModel
import com.kienvo.rosach.model.BookCategory

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    navController: NavController,
    searchViewModel: SearchViewModel = viewModel()
) {
    val headerColor = MaterialTheme.colorScheme.background
    val bodyColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)

    val categories by searchViewModel.categories.collectAsState()
    val isLoading by searchViewModel.isCategoriesLoading.collectAsState()

    val bannerImageSource = "https://as1.ftcdn.net/v2/jpg/03/70/42/66/1000_F_370426690_Pejt9KxjWTHPklsKwripaxr0iA17zupF.jpg"

    Scaffold(containerColor = headerColor) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .statusBarsPadding()
        ) {
            // HEADER
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Khám phá", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.AccountCircle, null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), modifier = Modifier.size(36.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(28.dp)).background(Color.White)
                        .clickable { navController.navigate("active_search") }.padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Tìm tên sách, tác giả...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // BODY
            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)).background(bodyColor)) {
                if (isLoading && categories.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp, 24.dp, 16.dp, 24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(categories.filter { it.slug != "top-thinh-hanh" }) { category ->
                            CategorySmallCard(category) {
                                when (category.slug) {
                                    "sach-chua-lanh" -> navController.navigate(Screen.SelfHelp.route)
                                    "top-ebook" -> navController.navigate(Screen.Ebook.route)
                                    "tieu-thuyet-trinh-tham" -> navController.navigate(Screen.Detective.route)
                                    "kids" -> navController.navigate(Screen.Kids.route)
                                    "astronomy" -> navController.navigate(Screen.Astronomy.route)
                                }
                            }
                        }
                        item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySmallCard(category: BookCategory, onClick: () -> Unit) {
    val bgMainColor = try { Color(android.graphics.Color.parseColor(category.color)) } catch (e: Exception) { Color(0xFF6D4C41) }
    
    // Hardcode image for Detective if it's not loading
    val displayImageUrl = if (category.slug == "tieu-thuyet-trinh-tham") {
        "https://cdn1.fahasa.com/media/catalog/product/n/h/nhung_vu_ky_an_cua_sherlock_holmestb_1_2020_05_30_12_53_10.jpg"
    } else {
        category.imageUrl
    }

    Box(
        modifier = Modifier
            .aspectRatio(1.6f) // Modern aspect ratio
            .clip(RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(colors = listOf(bgMainColor, bgMainColor.copy(alpha = 0.8f))))
            .clickable { onClick() }
    ) {
        if (displayImageUrl.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(displayImageUrl).crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(75.dp)
                    .height(105.dp)
                    .offset(x = 12.dp, y = 12.dp) // "Peek" effect: slightly offset outside
                    .rotate(-15f)
                    .alpha(0.9f)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Text(
                text = category.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                maxLines = 2
            )
        }
    }
}
