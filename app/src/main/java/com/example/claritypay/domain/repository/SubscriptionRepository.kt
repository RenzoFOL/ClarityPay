package com.example.claritypay.domain.repository

import com.example.claritypay.domain.models.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun observeSubscriptions(userId: Long): Flow<List<Subscription>>
    suspend fun addSubscription(subscription: Subscription)
    suspend fun updateSubscription(subscription: Subscription)
    suspend fun deleteSubscription(subscription: Subscription)
}