package com.kienvo.rosach.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kienvo.rosach.data.ListeningRecord
import com.kienvo.rosach.data.UserProfile
import com.kienvo.rosach.data.UserSettings
import com.kienvo.rosach.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    // Listen for auth state changes so profile reloads automatically
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _favoriteBooks = MutableStateFlow<List<String>>(emptyList())
    val favoriteBooks: StateFlow<List<String>> = _favoriteBooks.asStateFlow()

    private val _currentlyListening = MutableStateFlow<List<ListeningRecord>>(emptyList())
    val currentlyListening: StateFlow<List<ListeningRecord>> = _currentlyListening.asStateFlow()

    init {
        // Register auth listener to reload profile automatically when user signs in/out
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                loadUserProfile(uid)
            } else {
                // Clear profile on sign out
                _userProfile.value = null
                _favoriteBooks.value = emptyList()
                _currentlyListening.value = emptyList()
            }
        }
        authStateListener?.let { auth.addAuthStateListener(it) }

        // Tự động load user profile khi khởi tạo
        loadCurrentUserProfile()
    }

    override fun onCleared() {
        authStateListener?.let { auth.removeAuthStateListener(it) }
        super.onCleared()
    }

    // Load thông tin user hiện tại
    fun loadCurrentUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            loadUserProfile(userId)
        } else {
            Log.d("UserViewModel", "No user logged in")
        }
    }

    // Load user profile từ Firestore
    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = userRepository.getUserProfile(userId)
            result.onSuccess { profile ->
                _userProfile.value = profile
                _favoriteBooks.value = profile?.favoriteBookIds ?: emptyList()

                // Load currently listening books
                if (userId != null) {
                    val listening = userRepository.getCurrentlyListeningBooks(userId)
                    _currentlyListening.value = listening
                }
            }.onFailure { exception ->
                _error.value = exception.message
                Log.e("UserViewModel", "Error loading profile", exception)
            }

            _isLoading.value = false
        }
    }

    // Tạo profile cho user mới
    fun createUserProfile(email: String, username: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = userRepository.createUserProfile(userId, email, username)
            result.onSuccess { profile ->
                _userProfile.value = profile
                _favoriteBooks.value = profile.favoriteBookIds
            }.onFailure { exception ->
                _error.value = exception.message
            }

            _isLoading.value = false
        }
    }

    // Cập nhật username
    fun updateUsername(newUsername: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = userRepository.updateUsername(userId, newUsername)
            result.onSuccess {
                loadUserProfile(userId)
            }.onFailure { exception ->
                _error.value = exception.message
            }
            _isLoading.value = false
        }
    }

    // Cập nhật avatar từ Uri (chọn từ gallery)
    fun updateAvatar(uri: android.net.Uri) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val uploadResult = userRepository.uploadAvatar(userId, uri)
            
            uploadResult.onSuccess { url ->
                // Sau khi upload thành công Storage, cập nhật URL vào Firestore
                val updateResult = userRepository.updateAvatarUrl(userId, url)
                updateResult.onSuccess {
                    loadUserProfile(userId)
                }.onFailure { e ->
                    _error.value = "Lỗi cập nhật profile: ${e.message}"
                }
            }.onFailure { e ->
                _error.value = "Lỗi tải ảnh lên: ${e.message}"
            }
            _isLoading.value = false
        }
    }

    // Cập nhật avatar trực tiếp bằng URL (nếu có sẵn)
    fun updateAvatar(avatarUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = userRepository.updateAvatarUrl(userId, avatarUrl)
            result.onSuccess {
                loadUserProfile(userId)
            }.onFailure { exception ->
                _error.value = exception.message
            }
            _isLoading.value = false
        }
    }

    // Cập nhật settings
    fun updateSettings(settings: UserSettings) {
        val userId = auth.currentUser?.uid ?: return
        
        // Optimistic UI update: Cập nhật local ngay lập tức
        _userProfile.value = _userProfile.value?.copy(settings = settings)
        
        viewModelScope.launch {
            _isLoading.value = true
            val result = userRepository.updateSettings(userId, settings)
            if (result.isFailure) {
                // Nếu lỗi, load lại từ server để revert local state
                loadUserProfile(userId)
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }

    // Toggle dark mode
    fun toggleDarkMode() {
        val currentSettings = _userProfile.value?.settings ?: UserSettings()
        val newSettings = currentSettings.copy(isDarkMode = !currentSettings.isDarkMode)
        updateSettings(newSettings)
    }

    // Update playback speed
    fun updatePlaybackSpeed(speed: Float) {
        val currentSettings = _userProfile.value?.settings ?: return
        val newSettings = currentSettings.copy(playbackSpeed = speed)
        updateSettings(newSettings)
    }

    // Toggle notifications
    fun toggleNotifications() {
        val currentSettings = _userProfile.value?.settings ?: return
        val newSettings = currentSettings.copy(notificationsEnabled = !currentSettings.notificationsEnabled)
        updateSettings(newSettings)
    }

    // Toggle auto play
    fun toggleAutoPlay() {
        val currentSettings = _userProfile.value?.settings ?: return
        val newSettings = currentSettings.copy(autoPlay = !currentSettings.autoPlay)
        updateSettings(newSettings)
    }

    // Thêm sách vào yêu thích
    fun addFavoriteBook(bookId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = userRepository.addFavoriteBook(userId, bookId)
            result.onSuccess {
                loadUserProfile(userId)
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    // Xóa sách khỏi yêu thích
    fun removeFavoriteBook(bookId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = userRepository.removeFavoriteBook(userId, bookId)
            result.onSuccess {
                loadUserProfile(userId)
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    // Toggle favorite (thêm hoặc xóa)
    fun toggleFavorite(bookId: String) {
        if (_favoriteBooks.value.contains(bookId)) {
            removeFavoriteBook(bookId)
        } else {
            addFavoriteBook(bookId)
        }
    }

    // Kiểm tra sách có trong danh sách yêu thích không
    fun isFavorite(bookId: String): Boolean {
        return _favoriteBooks.value.contains(bookId)
    }

    // Cập nhật lịch sử nghe
    fun updateListeningProgress(
        bookId: String,
        bookTitle: String,
        lastPosition: Long,
        duration: Long,
        progress: Float
    ) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            // Lấy record cũ để cộng dồn totalListeningTime
            val oldRecord = userRepository.getListeningRecordForBook(userId, bookId)

            val record = ListeningRecord(
                bookId = bookId,
                bookTitle = bookTitle,
                lastPosition = lastPosition,
                duration = duration,
                progress = progress,
                lastListenedAt = System.currentTimeMillis(),
                totalListeningTime = oldRecord?.totalListeningTime ?: 0L
            )

            val result = userRepository.updateListeningHistory(userId, record)
            result.onSuccess {
                loadUserProfile(userId)
            }.onFailure { exception ->
                _error.value = exception.message
            }
        }
    }

    // Lấy vị trí nghe cuối của một cuốn sách
    fun getLastPosition(bookId: String): Long {
        return _userProfile.value?.listeningHistory
            ?.find { it.bookId == bookId }?.lastPosition ?: 0L
    }

    // Lấy progress của một cuốn sách
    fun getProgress(bookId: String): Float {
        return _userProfile.value?.listeningHistory
            ?.find { it.bookId == bookId }?.progress ?: 0f
    }

    // Đăng xuất
    fun logout() {
        auth.signOut()
        _userProfile.value = null
        _favoriteBooks.value = emptyList()
        _currentlyListening.value = emptyList()
    }

    // Clear error
    fun clearError() {
        _error.value = null
    }
}
