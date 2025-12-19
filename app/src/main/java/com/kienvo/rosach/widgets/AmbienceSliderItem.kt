package com.kienvo.rosach.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.kienvo.rosach.ui.theme.Yellow

// Widget con để vẽ thanh trượt (Slider) cho gọn code
@Composable
fun AmbienceSliderItem(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color.White, fontWeight = FontWeight.Medium)
            Text(text = "${(value * 100).toInt()}%", color = Color.Gray)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = Yellow,
                activeTrackColor = Yellow,
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}