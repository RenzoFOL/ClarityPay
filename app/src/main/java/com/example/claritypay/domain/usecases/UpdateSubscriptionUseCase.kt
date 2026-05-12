package com.example.claritypay.domain.usecases

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.Subscription
import com.example.claritypay.domain.repository.SubscriptionRepository

class UpdateSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: Subscription): AppResult<Unit> {
        return if (subscription.name.isBlank()) {
            AppResult.Error("El nombre no puede estar vacío")
        } else {
            repository.updateSubscription(subscription)
            AppResult.Success(Unit)
        }
    }
}