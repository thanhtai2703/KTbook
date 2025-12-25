package com.kienvo.rosach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kienvo.rosach.ui.theme.FonosCloneTheme
import com.kienvo.rosach.FonosApp
import com.kienvo.rosach.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userViewModel: UserViewModel = viewModel()
            val userProfile by userViewModel.userProfile.collectAsState()
            
            // Lấy preference từ Firestore, mặc định là True (Dark)
            val isDarkMode = userProfile?.settings?.isDarkMode ?: true

            FonosCloneTheme(darkTheme = isDarkMode) {
                FonosApp(userViewModel = userViewModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FonosCloneTheme {
        FonosApp()
    }
}