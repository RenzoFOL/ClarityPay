package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.usecases.AddTransactionUseCase
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetTransactionsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {
    val uiState: StateFlow<TransactionsUiState> = getCurrentUserUseCase()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(TransactionsUiState())
            } else {
                getTransactionsUseCase(user.id).map { transactions ->
                    TransactionsUiState(transactions = transactions)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TransactionsUiState())

    private val _actionState = MutableStateFlow<AppResult<Unit>?>(null)
    val actionState: StateFlow<AppResult<Unit>?> = _actionState.asStateFlow()

    fun addTransaction(title: String, amount: Double, category: String, dateLabel: String, type: String) {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase().firstOrNull()
            if (currentUser == null) {
                _actionState.update { AppResult.Error("No se pudo obtener el usuario actual") }
                return@launch
            }
            _actionState.value = addTransactionUseCase(
                Transaction(
                    id = 0,
                    userId = currentUser.id,
                    title = title,
                    category = category,
                    amount = amount,
                    type = type,
                    dateLabel = dateLabel.ifBlank { "Hoy" }
                )
            )
        }
    }
}
