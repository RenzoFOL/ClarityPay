package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.models.CategoryStatistic
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetStatisticsUseCase
import kotlinx.coroutines.flow.*

class StatisticsViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase
) : ViewModel() {

    // Combinamos el usuario activo con sus estadísticas
    val categoryStats: StateFlow<List<CategoryStatistic>> = getCurrentUserUseCase()
        .filterNotNull()
        .flatMapLatest { user ->
            getStatisticsUseCase(user.id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}