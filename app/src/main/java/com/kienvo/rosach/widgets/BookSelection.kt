package com.kienvo.rosach.widgets

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kienvo.rosach.model.Book

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookSection(
    title: String,
    books: List<Book>,
    onBookClick: (String) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        // 1. Tiêu đề kệ
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        // 2. Danh sách cuộn ngang (LazyRow)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(books) { book ->
                BookCard(
                    book = book,
                    onBookClick = onBookClick,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }
    }
}