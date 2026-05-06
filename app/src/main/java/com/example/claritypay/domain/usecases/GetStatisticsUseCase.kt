package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.CategoryStatistic
import com.example.claritypay.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class GetStatisticsUseCase(private val repository: FinanceRepository) {
    operator fun invoke(userId: Long): Flow<List<CategoryStatistic>> {
        return repository.observeCategoryStatistics(userId)
    }
}