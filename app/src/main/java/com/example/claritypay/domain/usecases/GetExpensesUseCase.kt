package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.ExpenseItem
import com.example.claritypay.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class GetExpensesUseCase(
    private val financeRepository: FinanceRepository
) {
    operator fun invoke(userId: Long): Flow<List<ExpenseItem>> =
        financeRepository.observeExpenses(userId)
}
