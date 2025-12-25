package com.kienvo.rosach.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.viewmodel.BookViewModel
import java.util.Locale

val SelfHelpBackground = Brush.verticalGradient(listOf(
    Color(0xFF2E3A4A),
    Color(0xFF28323E),
    Color(0xFF222933)
))

@Composable
fun SelfHelpScreen(
    navController: NavController,
    bookViewModel: BookViewModel = viewModel()
) {
    var isGridView by remember { mutableStateOf(false) }

    // Collect real data
    val booksState by bookViewModel.allBooks.collectAsState()
    
    // Filter locally for healing or popular
    val books = remember(booksState) {
        booksState.filter { item ->
            item.id.startsWith("healing_") || item.id.startsWith("popular_")
        }
    }

    LaunchedEffect(Unit) {
        if (booksState.isEmpty()) {
            bookViewModel.loadAllBooks()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = SelfHelpBackground)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape).size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Sách Self-Help", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(Color(0xFF1B1C20))
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tất cả sách", color = Color.Gray, fontSize = 16.sp)
                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(imageVector = if (isGridView) Icons.AutoMirrored.Filled.List else Icons.Filled.GridView, contentDescription = "Switch View", tint = Color.Gray, modifier = Modifier.size(24.dp))
                    }
                }

                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(books) { book ->
                            BookGridItem(book, onClick = { navController.navigate("detail/${book.id}") })
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                        items(books) { book ->
                            BookListItem(book, onClick = { navController.navigate("detail/${book.id}") })
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookGridItem(book: Book, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().height(240.dp)) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(book.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(book.author, color = Color.Gray, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun BookListItem(book: Book, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.width(100.dp).height(150.dp).clip(RoundedCornerShape(8.dp)))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(book.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(book.author, color = Color.Gray, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
