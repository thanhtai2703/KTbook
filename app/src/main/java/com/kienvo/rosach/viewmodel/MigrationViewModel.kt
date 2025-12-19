package com.kienvo.rosach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kienvo.rosach.service.DataMigrationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MigrationViewModel : ViewModel() {
    private val migrationService = DataMigrationService()

    private val _migrationState = MutableStateFlow<MigrationState>(MigrationState.Idle)
    val migrationState: StateFlow<MigrationState> = _migrationState.asStateFlow()

    sealed class MigrationState {
        object Idle : MigrationState()
        object Loading : MigrationState()
        data class Success(val message: String, val booksCount: Int, val categoriesCount: Int) : MigrationState()
        data class Error(val message: String) : MigrationState()
    }

    fun uploadData() {
        viewModelScope.launch {
            _migrationState.value = MigrationState.Loading
            try {
                val result = migrationService.uploadAllDataToFirestore()
                if (result.success) {
                    _migrationState.value = MigrationState.Success(
                        message = result.message,
                        booksCount = result.booksUploaded,
                        categoriesCount = result.categoriesCreated
                    )
                } else {
                    _migrationState.value = MigrationState.Error(result.message)
                }
            } catch (e: Exception) {
                _migrationState.value = MigrationState.Error("Lỗi: ${e.message}")
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            _migrationState.value = MigrationState.Loading
            try {
                val result = migrationService.clearAllData()
                if (result.success) {
                    _migrationState.value = MigrationState.Success(
                        message = result.message,
                        booksCount = 0,
                        categoriesCount = 0
                    )
                } else {
                    _migrationState.value = MigrationState.Error(result.message)
                }
            } catch (e: Exception) {
                _migrationState.value = MigrationState.Error("Lỗi: ${e.message}")
            }
        }
    }

    fun resetState() {
        _migrationState.value = MigrationState.Idle
    }
}

