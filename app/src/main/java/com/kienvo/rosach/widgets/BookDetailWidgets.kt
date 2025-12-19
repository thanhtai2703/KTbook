package com.kienvo.rosach.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kienvo.rosach.ui.theme.Yellow // Đảm bảo import đúng theme

// 1. Nút tròn (Yêu thích / Mixer)
@Composable
fun ActionCircleButton(icon: ImageVector, onClick: () -> Unit = {}) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(50.dp)
            .background(Color.White.copy(alpha = 0.1f), CircleShape)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
    }
}

// 2. Item thống kê (Sao, Thời lượng...)
@Composable
fun BookStatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = Yellow, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
    }
}

// 3. Dòng thông tin (NXB, Giọng đọc)
@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Gray, fontSize = 15.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

// 4. Item danh sách chương
@Composable
fun ChapterItem(index: Int, name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = String.format("%02d", index),
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.width(36.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, color = Color.White, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = "15 phút", color = Color.Gray, fontSize = 12.sp)
        }
        Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Gray)
    }
    Divider(color = Color.White.copy(alpha = 0.05f), thickness = 0.5.dp)
}

// 5. Tiêu đề mục
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

// 6. Đường kẻ phân cách
@Composable
fun MyDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))
}