package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.BalanceSummary
import com.example.claritypay.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class GetBalanceUseCase(
    private val financeRepository: FinanceRepository
) {
    operator fun invoke(userId: Long): Flow<BalanceSummary> =
        financeRepository.observeBalance(userId)
}
