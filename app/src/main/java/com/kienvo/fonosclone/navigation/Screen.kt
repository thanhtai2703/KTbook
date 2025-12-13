package com.kienvo.fonosclone.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object Personal : Screen("personal")
    object AudioPlayer : Screen("audio_player/{bookId}") {
        fun createRoute(bookId: String) = "audio_player/$bookId"
    }
}
