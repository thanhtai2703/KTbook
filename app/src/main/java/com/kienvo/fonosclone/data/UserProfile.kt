package com.kienvo.fonosclone.data

import com.google.firebase.Timestamp

// Class chứa toàn bộ thông tin User
data class UserProfile(
    val id: String = "",            // ID này sẽ trùng với UID của Firebase Auth
    val email: String = "",         // Email người dùng
    val username: String = "",      // Tên hiển thị (VD: "Người Yêu Sách")
    val avatarUrl: String = "",     // Link ảnh đại diện (lấy từ Firebase Storage/GitHub)
    val settings: UserSettings = UserSettings(), // Các cài đặt riêng (tách ra cho gọn)
    val favoriteBookIds: List<String> = emptyList(), // Danh sách ID sách yêu thích
    val listeningHistory: List<ListeningRecord> = emptyList(), // Lịch sử nghe
    val createdAt: Long = System.currentTimeMillis(), // Thời gian tạo tài khoản
    val lastUpdated: Long = System.currentTimeMillis() // Thời gian cập nhật cuối
)

// Class con chứa các cài đặt (Setting)
data class UserSettings(
    val isDarkMode: Boolean = true,        // Chế độ tối
    val notificationsEnabled: Boolean = true, // Nhận thông báo
    val playbackSpeed: Float = 1.0f,         // Tốc độ đọc mặc định (1.0, 1.25, 1.5...)
    val autoPlay: Boolean = false,           // Tự động phát tiếp
    val downloadQuality: String = "HIGH"     // Chất lượng tải xuống (LOW, MEDIUM, HIGH)
)

// Lịch sử nghe sách
data class ListeningRecord(
    val bookId: String = "",
    val bookTitle: String = "",
    val lastPosition: Long = 0L,      // Vị trí nghe cuối (milliseconds)
    val duration: Long = 0L,          // Tổng thời lượng sách
    val progress: Float = 0f,         // Tiến độ (0.0 - 1.0)
    val lastListenedAt: Long = System.currentTimeMillis(), // Lần nghe cuối
    val totalListeningTime: Long = 0L // Tổng thời gian đã nghe (milliseconds)
)