package com.kienvo.rosach.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.kienvo.rosach.repository.UserRepository
import com.kienvo.rosach.service.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authService = AuthService()
    private val userRepository = UserRepository()
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private var authStateListener: com.google.firebase.auth.FirebaseAuth.AuthStateListener? = null

    init {
        setupAuthStateListener()
    }

    private fun setupAuthStateListener() {
        _currentUser.value = auth.currentUser
        if (_currentUser.value != null) {
            _authState.value = AuthState.Authenticated
        }

        authStateListener = com.google.firebase.auth.FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user
            if (user == null) {
                _authState.value = AuthState.Idle
            } else {
                _authState.value = AuthState.Authenticated
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    override fun onCleared() {
        authStateListener?.let { auth.removeAuthStateListener(it) }
        super.onCleared()
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authService.loginWithEmail(email, password)
            result.onSuccess { user ->
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
                Log.d("AuthViewModel", "Login successful: ${user.email}")
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Đăng nhập thất bại")
                Log.e("AuthViewModel", "Login failed", exception)
            }
        }
    }

    fun registerWithEmail(email: String, password: String, username: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authService.registerWithEmail(email, password)
            result.onSuccess { user ->
                // Tạo user profile trong Firestore
                val profileResult = userRepository.createUserProfile(
                    userId = user.uid,
                    email = email,
                    username = username
                )

                profileResult.onSuccess {
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                    Log.d("AuthViewModel", "Registration successful: ${user.email}")
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Tạo profile thất bại")
                }
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Đăng ký thất bại")
                Log.e("AuthViewModel", "Registration failed", exception)
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authService.loginWithGoogle(idToken)
            result.onSuccess { user ->
                // Kiểm tra xem user đã có profile chưa
                val profileResult = userRepository.getUserProfile(user.uid)
                profileResult.onSuccess { profile ->
                    if (profile == null) {
                        // Tạo profile mới nếu chưa có
                        userRepository.createUserProfile(
                            userId = user.uid,
                            email = user.email ?: "",
                            username = user.displayName ?: "User"
                        )
                    }
                }

                _currentUser.value = user
                _authState.value = AuthState.Authenticated
                Log.d("AuthViewModel", "Google login successful: ${user.email}")
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Đăng nhập Google thất bại")
                Log.e("AuthViewModel", "Google login failed", exception)
            }
        }
    }

    fun logout() {
        authService.logout()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = authService.sendPasswordResetEmail(email)
            result.onSuccess {
                _authState.value = AuthState.PasswordResetSent
            }.onFailure { exception ->
                _authState.value = AuthState.Error(exception.message ?: "Gửi email thất bại")
            }
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object PasswordResetSent : AuthState()
    data class Error(val message: String) : AuthState()
}

