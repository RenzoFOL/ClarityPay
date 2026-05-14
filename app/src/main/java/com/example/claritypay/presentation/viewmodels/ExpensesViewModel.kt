package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.ExpenseItem
import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.usecases.AddTransactionUseCase
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetExpensesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpensesUiState(
    val expenses: List<ExpenseItem> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class ExpensesViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {
    val uiState: StateFlow<ExpensesUiState> = getCurrentUserUseCase()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(ExpensesUiState())
            } else {
                getExpensesUseCase(user.id).map { expenses ->
                    ExpensesUiState(expenses = expenses)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExpensesUiState())

    private val _actionState = MutableStateFlow<AppResult<Unit>?>(null)
    val actionState: StateFlow<AppResult<Unit>?> = _actionState.asStateFlow()

    fun addExpense(title: String, amount: Double, category: String, dateLabel: String) {
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
                    type = "EXPENSE",
                    dateLabel = dateLabel.ifBlank { "Hoy" }
                )
            )
        }
    }
}
