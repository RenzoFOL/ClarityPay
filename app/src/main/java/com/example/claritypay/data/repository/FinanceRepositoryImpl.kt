package com.example.claritypay.data.repository

import com.example.claritypay.data.local.dao.SubscriptionDao
import com.example.claritypay.data.local.dao.TransactionDao
import com.example.claritypay.data.local.entity.SubscriptionEntity
import com.example.claritypay.data.local.entity.TransactionEntity
import com.example.claritypay.domain.models.BalanceSummary
import com.example.claritypay.domain.models.CategoryStatistic
import com.example.claritypay.domain.models.ExpenseItem
import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class FinanceRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val subscriptionDao: SubscriptionDao
) : FinanceRepository {

    override fun observeBalance(userId: Long): Flow<BalanceSummary> =
        combine(
            transactionDao.observeTransactions(userId),
            subscriptionDao.observeSubscriptions(userId)
        ) { transactions, subscriptions ->
            val income = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
            val expenses = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount } +
                subscriptions.sumOf { it.monthlyAmount() }
            BalanceSummary(
                totalBalance = income - expenses,
                monthlyIncome = income,
                monthlyExpenses = expenses
            )
        }

    override fun observeExpenses(userId: Long): Flow<List<ExpenseItem>> =
        combine(
            transactionDao.observeExpenses(userId),
            subscriptionDao.observeSubscriptions(userId)
        ) { expenses, subscriptions ->
            val transactionGroups = expenses.groupBy { it.category }
            val subscriptionGroups = subscriptions.groupBy { it.category }
            (transactionGroups.keys + subscriptionGroups.keys)
                .map { category ->
                    val transactionItems = transactionGroups[category].orEmpty()
                    val subscriptionItems = subscriptionGroups[category].orEmpty()
                    ExpenseItem(
                        category = category,
                        totalAmount = transactionItems.sumOf { it.amount } +
                            subscriptionItems.sumOf { it.monthlyAmount() },
                        transactionsCount = transactionItems.size + subscriptionItems.size
                    )
                }
                .sortedByDescending { it.totalAmount }
        }

    override fun observeCategoryStatistics(userId: Long): Flow<List<CategoryStatistic>> =
        combine(
            transactionDao.observeCategorySummaries(userId),
            subscriptionDao.observeSubscriptions(userId)
        ) { summaries, subscriptions ->
            val subscriptionTotals = subscriptions
                .groupBy { it.category }
                .mapValues { (_, items) -> items.sumOf { it.monthlyAmount() } }

            (summaries.map { it.category }.toSet() + subscriptionTotals.keys)
                .map { category ->
                    val transactionTotal = summaries.firstOrNull { it.category == category }?.total ?: 0.0
                CategoryStatistic(
                        category = category,
                        totalAmount = transactionTotal + (subscriptionTotals[category] ?: 0.0)
                )
            }
                .sortedByDescending { it.totalAmount }
        }

    override fun observeRecentTransactions(userId: Long, limit: Int): Flow<List<Transaction>> =
        transactionDao.observeRecentTransactions(userId, limit).map { items ->
            items.map(TransactionEntity::toDomain)
        }

    override fun observeAllTransactions(userId: Long): Flow<List<Transaction>> =
        transactionDao.observeTransactions(userId).map { items ->
            items.map(TransactionEntity::toDomain)
        }

    override suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity())
    }
}

private fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    userId = userId,
    title = title,
    category = category,
    amount = amount,
    type = type,
    dateLabel = dateLabel
)

private fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    userId = userId,
    title = title,
    category = category,
    amount = amount,
    type = type,
    dateLabel = dateLabel
)

private fun SubscriptionEntity.monthlyAmount(): Double =
    if (period.equals("Anual", ignoreCase = true)) amount / 12.0 else amount
