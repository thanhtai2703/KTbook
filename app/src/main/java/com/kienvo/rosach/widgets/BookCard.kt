package com.kienvo.rosach.widgets

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookCard(
    book: Book,
    onBookClick: (String) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sourceKey: String = "default"
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .padding(end = 12.dp)
            .clickable { onBookClick(book.id) }
    ) {
        // 1. Ảnh bìa (Bo góc) với animation mượt mà
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .height(240.dp)
                .fillMaxWidth()
        ) {
            with(sharedTransitionScope){
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "book-${sourceKey}-${book.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = { _, _ ->
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Tên sách
        Text(
            text = book.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White
        )

        // 3. Tác giả
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFBBBBBB),
            maxLines = 1
        )
    }
}