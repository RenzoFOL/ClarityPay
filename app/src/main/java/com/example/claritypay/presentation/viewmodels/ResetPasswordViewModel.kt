package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.usecases.ResetPasswordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AppResult<Unit>?>(null)
    val uiState: StateFlow<AppResult<Unit>?> = _uiState

    fun resetPassword(email: String, newPass: String) {
        viewModelScope.launch {
            _uiState.value = resetPasswordUseCase(email, newPass)
        }
    }

    fun clearState() { _uiState.value = null }
}