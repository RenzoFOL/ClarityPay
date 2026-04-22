package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.SeedDemoDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionUiState(
    val isLoading: Boolean = true,
    val isAuthenticated: Boolean = false,
    val userId: Long? = null
)

class SessionViewModel(
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val seedDemoDataUseCase: SeedDemoDataUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            seedDemoDataUseCase()
            getCurrentUserUseCase().collect { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = user != null,
                        userId = user?.id
                    )
                }
            }
        }
    }
}
