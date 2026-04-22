package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.models.BalanceSummary
import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.usecases.GetBalanceUseCase
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetRecentTransactionsUseCase
import com.example.claritypay.domain.usecases.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Usuario",
    val balance: BalanceSummary? = null,
    val recentTransactions: List<Transaction> = emptyList()
)

class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBalanceUseCase: GetBalanceUseCase,
    private val getRecentTransactionsUseCase: GetRecentTransactionsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                if (user == null) return@collect
                _uiState.update { it.copy(userName = user.fullName) }

                launch {
                    getBalanceUseCase(user.id).collect { balance ->
                        _uiState.update { state -> state.copy(balance = balance) }
                    }
                }
                launch {
                    getRecentTransactionsUseCase(user.id).collect { transactions ->
                        _uiState.update { state -> state.copy(recentTransactions = transactions) }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
