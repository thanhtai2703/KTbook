package com.kienvo.rosach.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.kienvo.rosach.screens.ActiveSearchScreen
import com.kienvo.rosach.screens.AudioPlayerScreen
import com.kienvo.rosach.screens.ChatRecommendScreen
//import com.kienvo.rosach.screens.AstronomyAudioPlayerScreen
import com.kienvo.rosach.screens.AstronomyDetailScreen
import com.kienvo.rosach.screens.AstronomyScreen
import com.kienvo.rosach.screens.BigBannerDetailScreen
import com.kienvo.rosach.screens.AuthScreen
import com.kienvo.rosach.screens.BookDetailScreen
import com.kienvo.rosach.screens.DataMigrationScreen
import com.kienvo.rosach.screens.DetectiveScreen
import com.kienvo.rosach.screens.PersonalScreen
import com.kienvo.rosach.screens.PlaceholderScreen
import com.kienvo.rosach.screens.SearchScreen
import com.kienvo.rosach.widgets.BottomBar
import com.kienvo.rosach.widgets.MiniPlayer
import com.kienvo.rosach.screens.EbookScreen
import com.kienvo.rosach.screens.HomeScreen
//import com.kienvo.rosach.screens.KidAudioPlayerScreen
import com.kienvo.rosach.screens.KidBookDetailScreen
import com.kienvo.rosach.screens.KidsScreen
import com.kienvo.rosach.screens.LibraryScreen
import com.kienvo.rosach.screens.SelfHelpScreen
import com.kienvo.rosach.screens.settings.AboutScreen
import com.kienvo.rosach.screens.settings.ContactScreen
import com.kienvo.rosach.screens.settings.HelpCenterScreen
import com.kienvo.rosach.screens.settings.LanguageScreen
import com.kienvo.rosach.screens.settings.NotificationSettingsScreen
import com.kienvo.rosach.screens.settings.ProfileInfoScreen
import com.kienvo.rosach.screens.settings.ThemeSettingsScreen
import com.kienvo.rosach.viewmodel.AuthViewModel
import com.kienvo.rosach.viewmodel.UserViewModel
import com.kienvo.rosach.viewmodel.PlayerViewModel
import com.kienvo.rosach.viewmodel.LibraryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

private val mainTabs = listOf("home", "search", "library", "personal", "active_search")

private fun getTabIndex(route: String?): Int {
    return mainTabs.indexOf(route)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    playerViewModel: PlayerViewModel // Thêm PlayerViewModel
) {
    // Tạo shared LibraryViewModel instance
    val libraryViewModel: LibraryViewModel = viewModel()

    // Tăng thời gian lên 400-500ms và thêm Easing để lướt mượt hơn
    val animDuration = 400
    val slideSpec = tween<IntOffset>(durationMillis = animDuration, easing = FastOutSlowInEasing)
    val fadeSpec = tween<Float>(durationMillis = animDuration)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in mainTabs

    // Player states
    val currentBook by playerViewModel.currentBook.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val currentPosition by playerViewModel.currentPosition.collectAsState()
    val duration by playerViewModel.duration.collectAsState()
    val showMiniPlayer by playerViewModel.showMiniPlayer.collectAsState()

    val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f

    // Ẩn MiniPlayer khi đang ở màn hình player full screen
    val isInFullPlayer = currentRoute?.startsWith("audio_player") == true

    SharedTransitionLayout {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                bottomBar = {
                    Column {
                        // Mini Player nằm trên BottomBar
                        if (showMiniPlayer && !isInFullPlayer) {
                            MiniPlayer(
                                book = currentBook,
                                isPlaying = isPlaying,
                                currentPosition = progress,
                                onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                onCloseClick = { playerViewModel.closePlayer() },
                                onMiniPlayerClick = {
                                    // Mở lại màn hình player full screen (Unified)
                                    currentBook?.let { book ->
                                        navController.navigate("audio_player/${book.id}")
                                    }
                                }
                            )
                        }

                        // Bottom navigation bar
                        if (showBottomBar) {
                            BottomBar(navController)
                        }
                    }
                },
                containerColor = Color.Transparent
            ) { innerPadding ->

                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize(),

                    // --- 1. ENTER TRANSITION (Màn hình mới xuất hiện) ---
                    enterTransition = {
                        val targetRoute = targetState.destination.route
                        val fromIndex = getTabIndex(initialState.destination.route)
                        val toIndex = getTabIndex(targetState.destination.route)

                        // [LOGIC MỚI] Nếu đích đến là ActiveSearch -> Lướt từ Phải sang (Slide Left)
                        if (targetRoute == "active_search") {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                slideSpec
                            )
                        }
                        // Logic Tab cũ
                        else if (fromIndex != -1 && toIndex != -1) {
                            if (toIndex > fromIndex) slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                slideSpec
                            )
                            else slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                slideSpec
                            )
                        } else {
                            fadeIn(fadeSpec)
                        }
                    },

                    // --- 2. EXIT TRANSITION (Màn hình cũ biến mất) ---
                    exitTransition = {
                        val targetRoute = targetState.destination.route
                        val fromIndex = getTabIndex(initialState.destination.route)
                        val toIndex = getTabIndex(targetState.destination.route)

                        // [LOGIC MỚI] Nếu đang đi tới ActiveSearch -> Màn cũ lướt sang Trái
                        if (targetRoute == "active_search") {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                slideSpec
                            )
                        } else if (fromIndex != -1 && toIndex != -1) {
                            if (toIndex > fromIndex) slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                slideSpec
                            )
                            else slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                slideSpec
                            )
                        } else {
                            fadeOut(fadeSpec)
                        }
                    },

                    // --- 3. POP ENTER (Màn hình cũ quay lại khi bấm Back) ---
                    popEnterTransition = {
                        val initialRoute = initialState.destination.route
                        val fromIndex = getTabIndex(initialState.destination.route)
                        val toIndex = getTabIndex(targetState.destination.route)

                        // [LOGIC MỚI] Nếu quay lại từ ActiveSearch -> Màn cũ lướt từ Trái sang (Slide Right)
                        if (initialRoute == "active_search") {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                slideSpec
                            )
                        } else if (fromIndex != -1 && toIndex != -1) {
                            if (toIndex > fromIndex) slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                slideSpec
                            )
                            else slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                slideSpec
                            )
                        } else {
                            fadeIn(fadeSpec)
                        }
                    },

                    // --- 4. POP EXIT (Màn hình hiện tại biến mất khi bấm Back) ---
                    popExitTransition = {
                        val initialRoute = initialState.destination.route
                        val fromIndex = getTabIndex(initialState.destination.route)
                        val toIndex = getTabIndex(targetState.destination.route)

                        // [LOGIC MỚI] Nếu đang thoát ActiveSearch -> Nó lướt về bên Phải (Slide Right)
                        if (initialRoute == "active_search") {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                slideSpec
                            )
                        } else if (fromIndex != -1 && toIndex != -1) {
                            if (toIndex > fromIndex) slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                slideSpec
                            )
                            else slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                slideSpec
                            )
                        } else {
                            fadeOut(fadeSpec)
                        }
                    }
                ) {
                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            playerViewModel = playerViewModel
                        )
                    }

                    composable("auth") {
                        AuthScreen(
                            navController = navController,
                            authViewModel = authViewModel
                        )
                    }

                    composable("search") { SearchScreen(navController) }
                    composable("active_search") { ActiveSearchScreen(navController) }
                    composable("ai_chat") { ChatRecommendScreen(navController) }
                    composable("big_banner_detail") {
                        BigBannerDetailScreen(navController = navController)
                    }

                    // --- MÀN HÌNH CHI TIẾT ---
                    composable(
                        route = "detail/{bookId}",
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getString("bookId")
                        BookDetailScreen(
                            navController = navController,
                            bookId = bookId,
                            libraryViewModel = libraryViewModel,
                            playerViewModel = playerViewModel,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this
                        )
                    }

                    // New: Book detail with instant args
                    composable(
                        route = "detail/{id}?title={title}&author={author}&coverUrl={coverUrl}&sourceKey={sourceKey}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.StringType },
                            navArgument("title") { type = NavType.StringType; defaultValue = "" },
                            navArgument("author") { type = NavType.StringType; defaultValue = "" },
                            navArgument("coverUrl") { type = NavType.StringType; defaultValue = "" },
                            navArgument("sourceKey") { type = NavType.StringType; defaultValue = "" }
                        )
                    ) {
                        val args = it.arguments!!
                        val id = args.getString("id")
                        val title = args.getString("title")
                        val author = args.getString("author")
                        val coverUrl = args.getString("coverUrl")
                        val sourceKey = args.getString("sourceKey")

                        BookDetailScreen(
                            navController = navController,
                            bookId = id,
                            libraryViewModel = libraryViewModel,
                            playerViewModel = playerViewModel,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            initialTitle = title,
                            initialAuthor = author,
                            initialCoverUrl = coverUrl,
                            sourceKey = sourceKey
                        )
                    }

                    composable("library") {
                        LibraryScreen(
                            navController = navController,
                            libraryViewModel = libraryViewModel
                        )
                    }

                    composable(Screen.Ebook.route) { EbookScreen(navController) }
                    composable(Screen.Kids.route) { KidsScreen(navController) }
                    composable(Screen.SelfHelp.route) { SelfHelpScreen(navController) }
                    composable(Screen.Detective.route) { DetectiveScreen(navController) }
                    composable(Screen.Astronomy.route) { AstronomyScreen(navController) }
                    composable("personal") {
                        PersonalScreen(
                            navController = navController,
                            userViewModel = userViewModel,
                            authViewModel = authViewModel
                        )
                    }

                    // Also expose profile route so avatar -> navigate("profile") works
                    composable("profile") {
                        PersonalScreen(
                            navController = navController,
                            userViewModel = userViewModel,
                            authViewModel = authViewModel
                        )
                    }

                    // --- MÀN HÌNH PHÁT AUDIO ---
                    composable(
                        route = "audio_player/{bookId}",
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getString("bookId")
                        AudioPlayerScreen(
                            navController = navController,
                            bookId = bookId,
                            playerViewModel = playerViewModel
                        )
                    }

                    composable(
                        route = "kid_detail/{bookId}",
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getString("bookId")
                        KidBookDetailScreen(
                            navController = navController,
                            bookId = bookId,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            libraryViewModel = libraryViewModel
                        )
                    }

                    // --- MÀN HÌNH ADMIN MIGRATION ---
                    composable(Screen.DataMigration.route) {
                        DataMigrationScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    // Chi tiết sách thiên văn
                    composable(
                        route = "astronomy_detail/{bookId}",
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getString("bookId")
                        AstronomyDetailScreen(
                            navController = navController,
                            bookId = bookId,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this,
                            playerViewModel = playerViewModel,
                            libraryViewModel = libraryViewModel
                        )
                    }

                    // --- MÀN HÌNH CÀI ĐẶT ---
                    composable("settings/language") { LanguageScreen(navController) }
                    composable("settings/theme") { 
                        ThemeSettingsScreen(
                            navController = navController,
                            userViewModel = userViewModel
                        ) 
                    }
                    composable("settings/notification") { NotificationSettingsScreen(navController) }
                    composable("settings/about") { AboutScreen(navController) }
                    composable("settings/contact") { ContactScreen(navController) }
                    composable("settings/help_center") { HelpCenterScreen(navController) }
                    composable("settings/profile_info") { ProfileInfoScreen(navController) }
                }
            }
        }
    }
}