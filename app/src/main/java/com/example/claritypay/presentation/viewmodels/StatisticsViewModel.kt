package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.claritypay.domain.models.CategoryStatistic
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetStatisticsUseCase
import kotlinx.coroutines.flow.*

// --- IMPORTACIONES PARA EL GRÁFICO ---
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

class StatisticsViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase
) : ViewModel() {

    // 1. Variable que prepara los datos para que la pantalla pueda dibujar las barras
    val chartEntryModelProducer = ChartEntryModelProducer()

    // Combinamos el usuario activo con sus estadísticas
    val categoryStats: StateFlow<List<CategoryStatistic>> = getCurrentUserUseCase()
        .filterNotNull()
        .flatMapLatest { user ->
            getStatisticsUseCase(user.id)
        }
        .onEach { statistics ->
            // 2. Cada vez que lleguen datos nuevos, los convertimos al formato del gráfico
            val chartEntries = statistics.mapIndexed { index, stat ->
                // Nota: Si tu variable del monto se llama diferente (ej. stat.total), cámbialo aquí
                entryOf(index.toFloat(), stat.totalAmount.toFloat())
            }
            chartEntryModelProducer.setEntries(chartEntries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}