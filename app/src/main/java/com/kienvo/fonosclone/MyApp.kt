package com.kienvo.fonosclone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kienvo.fonosclone.navigation.AppNavigation
import com.kienvo.fonosclone.ui.theme.FonosCloneTheme
import com.kienvo.fonosclone.viewmodel.AuthViewModel
import com.kienvo.fonosclone.viewmodel.UserViewModel

@Composable
fun FonosApp() {
    val navController = rememberNavController()

    // Shared ViewModels for the entire app
    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    val currentUser by authViewModel.currentUser.collectAsState()

    // Auto-navigate based on auth state
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            // User not logged in, navigate to auth
            navController.navigate("auth") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            // User logged in, load their profile
            userViewModel.loadCurrentUserProfile()
        }
    }

    AppNavigation(
        navController = navController,
        authViewModel = authViewModel,
        userViewModel = userViewModel
    )
}

@Preview(showBackground = true)
@Composable
fun FonosAppPreview() {
    FonosCloneTheme {
        FonosApp()
    }
}
