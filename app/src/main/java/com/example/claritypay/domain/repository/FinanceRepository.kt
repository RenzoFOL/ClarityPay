package com.example.claritypay.domain.repository

import com.example.claritypay.domain.models.BalanceSummary
import com.example.claritypay.domain.models.CategoryStatistic
import com.example.claritypay.domain.models.ExpenseItem
import com.example.claritypay.domain.models.Transaction
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun observeBalance(userId: Long): Flow<BalanceSummary>
    fun observeExpenses(userId: Long): Flow<List<ExpenseItem>>
    fun observeRecentTransactions(userId: Long, limit: Int = 5): Flow<List<Transaction>>
    fun observeAllTransactions(userId: Long): Flow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction)

    fun observeCategoryStatistics(userId: Long): Flow<List<CategoryStatistic>>
}
