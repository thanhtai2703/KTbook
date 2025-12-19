package com.kienvo.rosach.widgets

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kienvo.rosach.ui.theme.DarkBg
import com.kienvo.rosach.ui.theme.PaleYellow

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf("Trang chủ", "Tìm kiếm", "Thư viện", "Cá nhân")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Search,
        Icons.Default.LibraryBooks,
        Icons.Default.Person
    )

    val routes = listOf(
        "home",
        "search",
        "library",
        "personal"
    )

    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)),
        containerColor = DarkBg,
        contentColor = Color.White
    ) {
        items.forEachIndexed { index, item ->
            val route = routes[index]

            // [LOGIC QUAN TRỌNG]: Kiểm tra để giữ icon sáng
            val isSelected = if (route == "search") {
                // Nếu là tab Tìm kiếm -> Nó sẽ sáng khi ở "search" HOẶC "active_search"
                currentRoute == "search" || currentRoute == "active_search"
            } else {
                currentRoute == route
            }

            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },

                // [SỬA] Dùng biến isSelected vừa tạo ở trên
                selected = isSelected,

                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DarkBg,
                    selectedTextColor = PaleYellow,
                    indicatorColor = PaleYellow,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}