package com.example.claritypay.data.repository

import com.example.claritypay.data.local.dao.SubscriptionDao
import com.example.claritypay.data.local.entity.SubscriptionEntity
import com.example.claritypay.domain.models.Subscription
import com.example.claritypay.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubscriptionRepositoryImpl(
    private val subscriptionDao: SubscriptionDao
) : SubscriptionRepository {

    override fun observeSubscriptions(userId: Long): Flow<List<Subscription>> =
        subscriptionDao.observeSubscriptions(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun addSubscription(subscription: Subscription) =
        subscriptionDao.insert(subscription.toEntity())

    override suspend fun updateSubscription(subscription: Subscription) =
        subscriptionDao.update(subscription.toEntity())

    override suspend fun deleteSubscription(subscription: Subscription) =
        subscriptionDao.delete(subscription.toEntity())
}

// Mappers
private fun SubscriptionEntity.toDomain() = Subscription(id, userId, name, amount, category, nextPaymentDate, period)
private fun Subscription.toEntity() = SubscriptionEntity(id, userId, name, amount, category, nextPaymentDate, period)