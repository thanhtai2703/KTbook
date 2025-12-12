package com.kienvo.fonosclone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

// ---------------- MODEL ----------------
data class BookRanked(
    val rank: Int,
    val title: String,
    val author: String,
    val coverUrl: String,
    val rating: Double
)

// ---------------- COLORS (FONOS DARK) ----------------
private val BgTop = Color(0xFF1C1626)
private val BgMid = Color(0xFF171225)
private val BgBottom = Color(0xFF0F0C1A)

private val Purple = Color(0xFF4B2E83)
private val PurpleSoft = Color(0xFF5C3B9D)

private val Card = Color(0xFF1E1B2E)
private val CardSoft = Color(0xFF25223A)

private val Accent = Color(0xFF6C63FF)

// ---------------- SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BigBannerDetailScreen(navController: NavController) {

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            BgTop,
            BgMid,
            BgBottom
        )
    )

    val topBooks = listOf(
        BookRanked(
            1,
            "The Call of the Wild",
            "Jack London",
            "https://images-na.ssl-images-amazon.com/images/I/811PTyrckTL.jpg",
            5.0
        ),
        BookRanked(
            2,
            "Sherlock Holmes",
            "Arthur Conan Doyle",
            "https://nxbhcm.com.vn/Image/Biasach/nhagiakimTB2020.jpg",
            5.0
        ),
        BookRanked(
            3,
            "Frankenstein",
            "Mary Shelley",
            "https://bizweb.dktcdn.net/thumb/1024x1024/100/363/455/products/motthoangtarucroonhangian011.jpg?v=1705552591463",
            4.5
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("active_search") }) {
                        Icon(Icons.Default.Search, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            item { BannerSectionFonos() }

            item {
                Column(modifier = Modifier.padding(top = 24.dp)) {
                    SectionTitle("Top Sách Tiếng Anh")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(topBooks) { book ->
                            BookRankedItemFonos(book)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }
}

// ---------------- COMPONENTS ----------------

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
    )
}

@Composable
fun BannerSectionFonos() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Purple, PurpleSoft),
                    start = Offset(0f, 0f),
                    end = Offset(1200f, 0f)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🇬🇧", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Sách Tiếng Anh",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Luyện tiếng Anh với 160 sách kinh điển từ Oxford University Press",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun BookRankedItemFonos(book: BookRanked) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .background(Card, RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.65f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardSoft)
            )

            Box(
                modifier = Modifier
                    .offset((-6).dp, (-6).dp)
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = book.rank.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            book.title,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            book.author,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                book.rating.toString(),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}
