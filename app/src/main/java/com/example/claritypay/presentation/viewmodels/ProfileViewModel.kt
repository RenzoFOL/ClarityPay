package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.User
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.UpdateProfileUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    // Estado del usuario observado desde la BD
    val user = getCurrentUserUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _uiState = MutableStateFlow<AppResult<Unit>?>(null)
    val uiState: StateFlow<AppResult<Unit>?> = _uiState

    fun updateProfile(fullName: String, bio: String?) {
        val currentUser = user.value ?: return
        viewModelScope.launch {
            val updatedUser = currentUser.copy(fullName = fullName, bio = bio)
            _uiState.value = updateProfileUseCase(updatedUser)
        }
    }

    fun clearState() { _uiState.value = null }
}