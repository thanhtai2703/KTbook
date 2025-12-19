package com.kienvo.rosach.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "AuthService"
    }

    // Lấy user hiện tại
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    // Kiểm tra đã đăng nhập chưa
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Đăng ký bằng email và password
    suspend fun registerWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.d(TAG, "User registered: ${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            Result.failure(e)
        }
    }

    // Đăng nhập bằng email và password
    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.d(TAG, "User logged in: ${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            Result.failure(e)
        }
    }

    // Đăng nhập bằng Google
    suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            if (user != null) {
                Log.d(TAG, "User logged in with Google: ${user.uid}")
                Result.success(user)
            } else {
                Result.failure(Exception("Google login failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google login error", e)
            Result.failure(e)
        }
    }

    // Đăng xuất
    fun logout() {
        auth.signOut()
        Log.d(TAG, "User logged out")
    }

    // Gửi email reset password
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent to: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending password reset email", e)
            Result.failure(e)
        }
    }

    // Cập nhật password
    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                user.updatePassword(newPassword).await()
                Log.d(TAG, "Password updated")
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating password", e)
            Result.failure(e)
        }
    }
}

