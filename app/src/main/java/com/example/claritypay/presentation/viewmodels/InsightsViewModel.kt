package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.models.Insight
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetInsightsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getInsightsUseCase: GetInsightsUseCase
) : ViewModel() {

    val financialInsights: StateFlow<List<Insight>> = getCurrentUserUseCase()
        .filterNotNull()
        .flatMapLatest { user ->
            getInsightsUseCase(user.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}