package com.kienvo.rosach.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book
import com.kienvo.rosach.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

@Composable
fun ActiveSearchScreen(
    navController: NavController,
    searchViewModel: SearchViewModel = viewModel()
) {
    val query by searchViewModel.searchQuery.collectAsState()
    val searchResults by searchViewModel.searchResults.collectAsState()
    val isSearching by searchViewModel.isSearching.collectAsState()
    val trendingSearches by searchViewModel.trendingSearches.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val headerColor = Color(0xFF0F1015)
    val contentBoxColor = Color(0xFF13161F)

    val displayBooks = if (query.isEmpty()) trendingSearches else searchResults

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        containerColor = headerColor,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().background(headerColor).statusBarsPadding()
                    .padding(top = 28.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).height(40.dp).clip(RoundedCornerShape(24.dp))
                        .background(Color.White).padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) Text("Tìm tên sách, tác giả...", color = Color.Gray, fontSize = 14.sp)
                        BasicTextField(
                            value = query,
                            onValueChange = { searchViewModel.onQueryChange(it) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp),
                            cursorBrush = SolidColor(headerColor),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                        )
                    }
                    if (query.isNotEmpty()) {
                        Icon(Icons.Default.Close, null, tint = Color.Gray, modifier = Modifier.size(20.dp).clickable { searchViewModel.onQueryChange("") })
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Hủy", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { navController.popBackStack() })
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)).background(contentBoxColor)) {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(span = { GridItemSpan(3) }) {
                            Text(
                                text = if (query.isEmpty()) "Được tìm kiếm nhiều" else "Kết quả tìm kiếm",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                            )
                        }

                        itemsIndexed(displayBooks) { index, book ->
                            AnimatedGridItem(index = index) {
                                SearchGridItem(book) {
                                    navController.navigate("detail/${book.id}")
                                }
                            }
                        }
                        item(span = { GridItemSpan(3) }) { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchGridItem(book: Book, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(book.coverUrl).crossfade(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().aspectRatio(0.7f).clip(RoundedCornerShape(8.dp)).background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = book.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(text = book.author, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun AnimatedGridItem(index: Int, content: @Composable () -> Unit) {
    val alphaAnim = remember { Animatable(0f) }
    val yAnim = remember { Animatable(50f) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        alphaAnim.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        yAnim.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
    }
    Box(modifier = Modifier.graphicsLayer { alpha = alphaAnim.value; translationY = yAnim.value }) { content() }
}
