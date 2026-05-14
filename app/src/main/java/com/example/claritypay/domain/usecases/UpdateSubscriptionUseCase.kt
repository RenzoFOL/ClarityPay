package com.example.claritypay.domain.usecases

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.Subscription
import com.example.claritypay.domain.repository.SubscriptionRepository

class UpdateSubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: Subscription): AppResult<Unit> {
        return when {
            subscription.name.isBlank() -> AppResult.Error("El nombre no puede estar vacio")
            subscription.amount <= 0 -> AppResult.Error("El monto debe ser mayor a 0")
            subscription.nextPaymentDate.isBlank() -> AppResult.Error("Debes seleccionar una fecha de pago")
            else -> {
                repository.updateSubscription(subscription)
                AppResult.Success(Unit)
            }
        }
    }
}
