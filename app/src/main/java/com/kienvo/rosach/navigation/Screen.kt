package com.kienvo.rosach.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Library : Screen("library")
    object Personal : Screen("personal")
    object AudioPlayer : Screen("audio_player/{bookId}") {
        fun createRoute(bookId: String) = "audio_player/$bookId"
    }
    object Ebook : Screen("ebook")
    object Kids : Screen("kids")
    object SelfHelp : Screen("self_help")
    object DataMigration : Screen("data_migration")
}
