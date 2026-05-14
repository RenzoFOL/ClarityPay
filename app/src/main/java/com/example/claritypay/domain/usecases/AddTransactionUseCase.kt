package com.example.claritypay.domain.usecases

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.repository.FinanceRepository

class AddTransactionUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(transaction: Transaction): AppResult<Unit> {
        return when {
            transaction.title.isBlank() -> AppResult.Error("El nombre del movimiento es obligatorio")
            transaction.category.isBlank() -> AppResult.Error("La categoria es obligatoria")
            transaction.amount <= 0 -> AppResult.Error("El monto debe ser mayor a 0")
            transaction.type !in listOf("EXPENSE", "INCOME") -> AppResult.Error("Tipo de movimiento invalido")
            else -> {
                repository.addTransaction(transaction)
                AppResult.Success(Unit)
            }
        }
    }
}
