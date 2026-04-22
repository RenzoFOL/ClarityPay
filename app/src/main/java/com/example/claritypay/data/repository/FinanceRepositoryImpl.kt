package com.example.claritypay.data.repository

import com.example.claritypay.data.local.dao.TransactionDao
import com.example.claritypay.data.local.entity.TransactionEntity
import com.example.claritypay.domain.models.BalanceSummary
import com.example.claritypay.domain.models.ExpenseItem
import com.example.claritypay.domain.models.Transaction
import com.example.claritypay.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinanceRepositoryImpl(
    private val transactionDao: TransactionDao
) : FinanceRepository {

    override fun observeBalance(userId: Long): Flow<BalanceSummary> =
        transactionDao.observeTransactions(userId).map { transactions ->
            val income = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
            val expenses = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            BalanceSummary(
                totalBalance = income - expenses,
                monthlyIncome = income,
                monthlyExpenses = expenses
            )
        }

    override fun observeExpenses(userId: Long): Flow<List<ExpenseItem>> =
        transactionDao.observeExpenses(userId).map { expenses ->
            expenses.groupBy { it.category }
                .map { (category, items) ->
                    ExpenseItem(
                        category = category,
                        totalAmount = items.sumOf { it.amount },
                        transactionsCount = items.size
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
}

private fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    title = title,
    category = category,
    amount = amount,
    type = type,
    dateLabel = dateLabel
)
