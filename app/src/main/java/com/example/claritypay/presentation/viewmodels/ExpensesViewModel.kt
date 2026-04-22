package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.models.ExpenseItem
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetExpensesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExpensesUiState(
    val expenses: List<ExpenseItem> = emptyList()
)

class ExpensesViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpensesUiState())
    val uiState: StateFlow<ExpensesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { user ->
                if (user == null) return@collect
                getExpensesUseCase(user.id).collect { expenses ->
                    _uiState.update { it.copy(expenses = expenses) }
                }
            }
        }
    }
}
