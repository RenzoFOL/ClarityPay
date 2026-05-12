package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.Subscription
import com.example.claritypay.domain.repository.SubscriptionRepository

class DeleteSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: Subscription) {
        repository.deleteSubscription(subscription)
    }
}