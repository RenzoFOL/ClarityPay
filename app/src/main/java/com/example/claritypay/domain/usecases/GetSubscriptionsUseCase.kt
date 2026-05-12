package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.Subscription
import com.example.claritypay.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow

class GetSubscriptionsUseCase(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(userId: Long): Flow<List<Subscription>> {
        return repository.observeSubscriptions(userId)
    }
}