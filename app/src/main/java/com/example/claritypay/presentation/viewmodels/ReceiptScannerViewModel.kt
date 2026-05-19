package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.ScannedReceipt
import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.receipt.ReceiptParser
import com.example.claritypay.domain.usecases.AddTransactionUseCase
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReceiptScannerUiState(
    val isProcessing: Boolean = false,
    val draft: ScannedReceipt? = null,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class ReceiptScannerViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReceiptScannerUiState())
    val uiState: StateFlow<ReceiptScannerUiState> = _uiState.asStateFlow()

    fun onReceiptDetected(rawText: String) {
        if (_uiState.value.draft != null) return

        _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
        val parsedReceipt = ReceiptParser.parse(rawText)
        if (parsedReceipt.amount <= 0.0) {
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    errorMessage = "No pude encontrar el total del ticket. Intenta acercar la camara."
                )
            }
            return
        }

        _uiState.update {
            it.copy(isProcessing = false, draft = parsedReceipt)
        }
    }

    fun saveReceipt(title: String, amount: Double, category: String, dateLabel: String) {
        viewModelScope.launch {
            if (title.isBlank() || amount <= 0.0) {
                _uiState.update {
                    it.copy(errorMessage = "Revisa el nombre y el total antes de guardar.")
                }
                return@launch
            }

            val currentUser = getCurrentUserUseCase().firstOrNull()
            if (currentUser == null) {
                _uiState.update {
                    it.copy(errorMessage = "No se pudo obtener el usuario actual.")
                }
                return@launch
            }

            when (
                val result = addTransactionUseCase(
                    Transaction(
                        id = 0,
                        userId = currentUser.id,
                        title = title.trim(),
                        category = category,
                        amount = amount,
                        type = "EXPENSE",
                        dateLabel = dateLabel.ifBlank { "Hoy" }
                    )
                )
            ) {
                is AppResult.Success -> _uiState.update { it.copy(isSaved = true, errorMessage = null) }
                is AppResult.Error -> _uiState.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    fun resetScanner() {
        _uiState.value = ReceiptScannerUiState()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
