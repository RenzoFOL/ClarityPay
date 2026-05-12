package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.Subscription
import com.example.claritypay.domain.usecases.*
import kotlinx.coroutines.ExperimentalCoroutinesApi // Importante para flatMapLatest
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class) // flatMapLatest es una API experimental en algunas versiones
class SubscriptionsViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val addSubscriptionUseCase: AddSubscriptionUseCase,
    private val updateSubscriptionUseCase: UpdateSubscriptionUseCase,
    private val deleteSubscriptionUseCase: DeleteSubscriptionUseCase
) : ViewModel() {

    // 1. Obtenemos las suscripciones del usuario activo de forma reactiva
    val subscriptions: StateFlow<List<Subscription>> = getCurrentUserUseCase()
        .filterNotNull()
        .flatMapLatest { user ->
            getSubscriptionsUseCase(user.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<AppResult<Unit>?>(null)
    val uiState: StateFlow<AppResult<Unit>?> = _uiState

    fun addSubscription(name: String, amount: Double, category: String, date: String, period: String) {
        viewModelScope.launch {
            // CORRECCIÓN: Obtenemos el usuario actual recolectando el primer valor del Flow
            val currentUser = getCurrentUserUseCase().firstOrNull()

            if (currentUser != null) {
                val newSub = Subscription(
                    userId = currentUser.id,
                    name = name,
                    amount = amount,
                    category = category,
                    nextPaymentDate = date,
                    period = period
                )
                _uiState.value = addSubscriptionUseCase(newSub)
            } else {
                _uiState.value = AppResult.Error("No se pudo obtener el usuario actual")
            }
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            deleteSubscriptionUseCase(subscription)
        }
    }

    fun clearState() {
        _uiState.value = null
    }
}