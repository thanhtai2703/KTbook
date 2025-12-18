package com.kienvo.fonosclone.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kienvo.fonosclone.data.ListeningRecord
import com.kienvo.fonosclone.data.UserProfile
import com.kienvo.fonosclone.data.UserSettings
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    companion object {
        private const val TAG = "UserRepository"
    }

    // Lấy UID của user hiện tại
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // Tạo profile mới cho user vừa đăng ký
    suspend fun createUserProfile(userId: String, email: String, username: String): Result<UserProfile> {
        return try {
            val newProfile = UserProfile(
                id = userId,
                email = email,
                username = username,
                avatarUrl = "",
                settings = UserSettings(),
                favoriteBookIds = emptyList(),
                listeningHistory = emptyList(),
                createdAt = System.currentTimeMillis(),
                lastUpdated = System.currentTimeMillis()
            )

            usersCollection.document(userId).set(newProfile).await()
            Log.d(TAG, "User profile created successfully for $userId")
            Result.success(newProfile)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user profile", e)
            Result.failure(e)
        }
    }

    // Lấy thông tin profile của user
    suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            val profile = snapshot.toObject(UserProfile::class.java)
            Log.d(TAG, "User profile retrieved: ${profile?.username}")
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile", e)
            Result.failure(e)
        }
    }

    // Cập nhật toàn bộ profile
    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val updatedProfile = userProfile.copy(lastUpdated = System.currentTimeMillis())
            usersCollection.document(userProfile.id).set(updatedProfile).await()
            Log.d(TAG, "User profile updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            Result.failure(e)
        }
    }

    // Cập nhật username
    suspend fun updateUsername(userId: String, newUsername: String): Result<Unit> {
        return try {
            usersCollection.document(userId).update(
                mapOf(
                    "username" to newUsername,
                    "lastUpdated" to System.currentTimeMillis()
                )
            ).await()
            Log.d(TAG, "Username updated to: $newUsername")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating username", e)
            Result.failure(e)
        }
    }

    // Cập nhật avatar URL
    suspend fun updateAvatarUrl(userId: String, avatarUrl: String): Result<Unit> {
        return try {
            usersCollection.document(userId).update(
                mapOf(
                    "avatarUrl" to avatarUrl,
                    "lastUpdated" to System.currentTimeMillis()
                )
            ).await()
            Log.d(TAG, "Avatar URL updated")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating avatar URL", e)
            Result.failure(e)
        }
    }

    // Cập nhật settings
    suspend fun updateSettings(userId: String, settings: UserSettings): Result<Unit> {
        return try {
            usersCollection.document(userId).update(
                mapOf(
                    "settings" to settings,
                    "lastUpdated" to System.currentTimeMillis()
                )
            ).await()
            Log.d(TAG, "Settings updated")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating settings", e)
            Result.failure(e)
        }
    }

    // Thêm sách vào danh sách yêu thích
    suspend fun addFavoriteBook(userId: String, bookId: String): Result<Unit> {
        return try {
            val profile = getUserProfile(userId).getOrNull()
            if (profile != null) {
                val updatedFavorites = profile.favoriteBookIds.toMutableList()
                if (!updatedFavorites.contains(bookId)) {
                    updatedFavorites.add(bookId)
                    usersCollection.document(userId).update(
                        mapOf(
                            "favoriteBookIds" to updatedFavorites,
                            "lastUpdated" to System.currentTimeMillis()
                        )
                    ).await()
                    Log.d(TAG, "Book $bookId added to favorites")
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding favorite book", e)
            Result.failure(e)
        }
    }

    // Xóa sách khỏi danh sách yêu thích
    suspend fun removeFavoriteBook(userId: String, bookId: String): Result<Unit> {
        return try {
            val profile = getUserProfile(userId).getOrNull()
            if (profile != null) {
                val updatedFavorites = profile.favoriteBookIds.toMutableList()
                updatedFavorites.remove(bookId)
                usersCollection.document(userId).update(
                    mapOf(
                        "favoriteBookIds" to updatedFavorites,
                        "lastUpdated" to System.currentTimeMillis()
                    )
                ).await()
                Log.d(TAG, "Book $bookId removed from favorites")
                Result.success(Unit)
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing favorite book", e)
            Result.failure(e)
        }
    }

    // Kiểm tra sách có trong danh sách yêu thích không
    suspend fun isFavoriteBook(userId: String, bookId: String): Boolean {
        return try {
            val profile = getUserProfile(userId).getOrNull()
            profile?.favoriteBookIds?.contains(bookId) ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking favorite book", e)
            false
        }
    }

    // Cập nhật lịch sử nghe
    suspend fun updateListeningHistory(userId: String, record: ListeningRecord): Result<Unit> {
        return try {
            val profile = getUserProfile(userId).getOrNull()
            if (profile != null) {
                val updatedHistory = profile.listeningHistory.toMutableList()

                // Tìm và cập nhật record cũ hoặc thêm mới
                val existingIndex = updatedHistory.indexOfFirst { it.bookId == record.bookId }
                if (existingIndex >= 0) {
                    updatedHistory[existingIndex] = record
                } else {
                    updatedHistory.add(record)
                }

                usersCollection.document(userId).update(
                    mapOf(
                        "listeningHistory" to updatedHistory,
                        "lastUpdated" to System.currentTimeMillis()
                    )
                ).await()
                Log.d(TAG, "Listening history updated for book ${record.bookId}")
                Result.success(Unit)
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating listening history", e)
            Result.failure(e)
        }
    }

    // Lấy lịch sử nghe của một cuốn sách cụ thể
    suspend fun getListeningRecordForBook(userId: String, bookId: String): ListeningRecord? {
        return try {
            val profile = getUserProfile(userId).getOrNull()
            profile?.listeningHistory?.find { it.bookId == bookId }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting listening record", e)
            null
        }
    }

    // Lấy danh sách sách đang nghe (progress < 1.0)
    suspend fun getCurrentlyListeningBooks(userId: String): List<ListeningRecord> {
        return try {
            val profile = getUserProfile(userId).getOrNull()
            profile?.listeningHistory
                ?.filter { it.progress > 0 && it.progress < 1.0f }
                ?.sortedByDescending { it.lastListenedAt }
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting currently listening books", e)
            emptyList()
        }
    }

    // Lấy danh sách sách đã nghe xong (progress >= 1.0)
    suspend fun getCompletedBooks(userId: String): List<ListeningRecord> {
        return try {
            val profile = getUserProfile(userId).getOrNull()
            profile?.listeningHistory
                ?.filter { it.progress >= 1.0f }
                ?.sortedByDescending { it.lastListenedAt }
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting completed books", e)
            emptyList()
        }
    }

    // Xóa tài khoản user
    suspend fun deleteUserAccount(userId: String): Result<Unit> {
        return try {
            usersCollection.document(userId).delete().await()
            auth.currentUser?.delete()?.await()
            Log.d(TAG, "User account deleted")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user account", e)
            Result.failure(e)
        }
    }
}

