package com.kienvo.fonosclone.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.kienvo.fonosclone.screens.ActiveSearchScreen
import com.kienvo.fonosclone.screens.AudioPlayerScreen
import com.kienvo.fonosclone.screens.BigBannerDetailScreen
import com.kienvo.fonosclone.screens.BookDetailScreen
import com.kienvo.fonosclone.screens.FonosHomeScreen
import com.kienvo.fonosclone.screens.PersonalScreen
import com.kienvo.fonosclone.screens.PlaceholderScreen
import com.kienvo.fonosclone.screens.SearchScreen
import com.kienvo.fonosclone.widgets.BottomBar
import com.kienvo.fonosclone.screens.EbookScreen

private val mainTabs = listOf("home", "search", "library", "personal", "active_search")

private fun getTabIndex(route: String?): Int {
    return mainTabs.indexOf(route)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(navController: NavHostController) {
    // Tăng thời gian lên 400-500ms và thêm Easing để lướt mượt hơn
    val animDuration = 400
    val slideSpec = tween<IntOffset>(durationMillis = animDuration, easing = FastOutSlowInEasing)
    val fadeSpec = tween<Float>(durationMillis = animDuration)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in mainTabs

    SharedTransitionLayout {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(navController)
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
                    FonosHomeScreen(
                        navController = navController,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this
                    )
                }
                composable("search") { SearchScreen(navController) }
                composable("active_search") { ActiveSearchScreen(navController) }
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
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this
                    )
                }

                composable("library") { PlaceholderScreen("Thư viện", navController) }
                composable(Screen.Ebook.route) { EbookScreen(navController) }
                composable("personal") { PersonalScreen(navController) }

                // --- MÀN HÌNH PHÁT AUDIO ---
                composable(
                    route = "audio_player/{bookId}",
                    arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId")
                    AudioPlayerScreen(
                        navController = navController,
                        bookId = bookId
                    )
                }
            }
        }
    }
}