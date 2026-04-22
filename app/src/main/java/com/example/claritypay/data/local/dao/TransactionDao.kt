package com.example.claritypay.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.claritypay.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY id DESC")
    fun observeTransactions(userId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = 'EXPENSE' ORDER BY id DESC")
    fun observeExpenses(userId: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY id DESC LIMIT :limit")
    fun observeRecentTransactions(userId: Long, limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId")
    suspend fun countByUser(userId: Long): Int
}
