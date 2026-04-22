package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class GetRecentTransactionsUseCase(
    private val financeRepository: FinanceRepository
) {
    operator fun invoke(userId: Long, limit: Int = 5): Flow<List<Transaction>> =
        financeRepository.observeRecentTransactions(userId, limit)
}
