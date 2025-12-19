package com.kienvo.rosach.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kienvo.rosach.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbienceBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAiClick: () -> Unit,
    rainVol: Float, onRainChange: (Float) -> Unit,
    fireVol: Float, onFireChange: (Float) -> Unit,
    cafeVol: Float, onCafeChange: (Float) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1E1E1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .padding(bottom = 30.dp)
        ) {
            Text("Không gian đọc sách", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))

            // Nút AI Gợi ý
            Button(
                onClick = onAiClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A), contentColor = Yellow),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Gợi ý theo nội dung sách")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Các thanh trượt (Tái sử dụng AmbienceSliderItem đã tách trước đó)
            AmbienceSliderItem("Tiếng Mưa Rơi", rainVol, onRainChange)
            Spacer(modifier = Modifier.height(16.dp))
            AmbienceSliderItem("Bếp Lửa Trại", fireVol, onFireChange)
            Spacer(modifier = Modifier.height(16.dp))
            AmbienceSliderItem("Quán Cà Phê", cafeVol, onCafeChange)
        }
    }
}