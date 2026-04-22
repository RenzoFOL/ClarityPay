package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(
    private val financeRepository: FinanceRepository
) {
    operator fun invoke(userId: Long): Flow<List<Transaction>> =
        financeRepository.observeAllTransactions(userId)
}
