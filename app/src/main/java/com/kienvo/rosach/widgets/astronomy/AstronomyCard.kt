package com.kienvo.rosach.widgets.astronomy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kienvo.rosach.model.Book

/**
 * Card hiển thị sách thiên văn
 */
@Composable
fun AstronomyCard(book: Book) {
    Column {
        // Ảnh bìa bo tròn
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(book.coverUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // Hình vuông
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tên sách
        Text(
            text = book.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Tác giả (Icon + Text)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.StarRate,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = book.author,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

