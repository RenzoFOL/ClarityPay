package com.example.claritypay.data.local.dao

import androidx.room.*
import com.example.claritypay.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE userId = :userId ORDER BY nextPaymentDate ASC")
    fun observeSubscriptions(userId: Long): Flow<List<SubscriptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: SubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subscriptions: List<SubscriptionEntity>)

    @Update
    suspend fun update(subscription: SubscriptionEntity)

    @Delete
    suspend fun delete(subscription: SubscriptionEntity)

    @Query("SELECT COUNT(*) FROM subscriptions WHERE userId = :userId")
    suspend fun countByUser(userId: Long): Int
}
