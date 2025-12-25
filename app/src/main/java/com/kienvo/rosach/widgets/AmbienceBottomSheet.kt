package com.kienvo.rosach.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kienvo.rosach.ui.theme.Yellow
import com.kienvo.rosach.viewmodel.PlayerViewModel
import com.kienvo.rosach.service.GeminiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbienceBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    playerViewModel: PlayerViewModel,
    bookDescription: String
) {
    val scope = rememberCoroutineScope()
    val geminiService = remember { GeminiService() }
    var isThinking by remember { mutableStateOf(false) }

    // Collect global volumes from ViewModel
    val rainVol by playerViewModel.rainVol.collectAsState()
    val windVol by playerViewModel.windVol.collectAsState()
    val fireVol by playerViewModel.fireVol.collectAsState()
    val birdsVol by playerViewModel.birdsVol.collectAsState()

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

            // AI Suggestion Button
            Button(
                onClick = {
                    scope.launch {
                        isThinking = true
                        val jsonSuggestion = geminiService.getAmbientSuggestion(bookDescription)
                        playerViewModel.setAmbientMix(jsonSuggestion)
                        isThinking = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A), contentColor = Yellow),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isThinking
            ) {
                if (isThinking) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Yellow, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Gợi ý theo nội dung sách")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sliders connected to ViewModel
            AmbienceSliderItem("Tiếng Mưa Rơi", rainVol) {
                playerViewModel.setAmbientType("rain")
                playerViewModel.updateAmbientVol("rain", it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            AmbienceSliderItem("Tiếng Gió Thổi", windVol) {
                playerViewModel.setAmbientType("wind")
                playerViewModel.updateAmbientVol("wind", it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            AmbienceSliderItem("Bếp Lửa Trại", fireVol) {
                playerViewModel.setAmbientType("fire")
                playerViewModel.updateAmbientVol("fire", it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            AmbienceSliderItem("Tiếng Chim Hót", birdsVol) {
                playerViewModel.setAmbientType("birds")
                playerViewModel.updateAmbientVol("birds", it)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = { 
                    playerViewModel.stopAmbient() 
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tắt tất cả âm thanh nền", color = Color.Gray)
            }
        }
    }
}