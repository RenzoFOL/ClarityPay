package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.models.BalanceSummary
import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.usecases.GetBalanceUseCase
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetRecentTransactionsUseCase
import com.example.claritypay.domain.usecases.LogoutUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Usuario",
    val balance: BalanceSummary? = null,
    val recentTransactions: List<Transaction> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBalanceUseCase: GetBalanceUseCase,
    private val getRecentTransactionsUseCase: GetRecentTransactionsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = getCurrentUserUseCase()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(HomeUiState())
            } else {
                combine(
                    getBalanceUseCase(user.id),
                    getRecentTransactionsUseCase(user.id)
                ) { balance, transactions ->
                    HomeUiState(
                        userName = user.fullName,
                        balance = balance,
                        recentTransactions = transactions
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
