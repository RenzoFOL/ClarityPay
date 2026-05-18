package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.Insight
import com.example.claritypay.domain.models.InsightType
import com.example.claritypay.domain.repository.FinanceRepository
import com.example.claritypay.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetInsightsUseCase(
    private val financeRepository: FinanceRepository,
    private val subscriptionRepository: SubscriptionRepository
) {
    operator fun invoke(userId: Long): Flow<List<Insight>> {
        return combine(
            financeRepository.observeBalance(userId),
            financeRepository.observeCategoryStatistics(userId),
            subscriptionRepository.observeSubscriptions(userId)
        ) { balance, stats, subscriptions ->
            val insights = mutableListOf<Insight>()

            // 1. Análisis de salud del Balance General
            if (balance.totalBalance > 0) {
                insights.add(
                    Insight(
                        title = "Balance Saludable",
                        description = "Tus ingresos superan tus gastos este mes. Mantienes un flujo de caja óptimo para metas de inversión.",
                        type = InsightType.SUCCESS,
                        valueLabel = "+$${String.format("%.2f", balance.totalBalance)}"
                    )
                )
            } else if (balance.totalBalance < 0) {
                insights.add(
                    Insight(
                        title = "Alerta de Sobregiro",
                        description = "Tus egresos han superado los ingresos. Te recomendamos limitar compras no esenciales esta semana.",
                        type = InsightType.WARNING,
                        valueLabel = "$${String.format("%.2f", balance.totalBalance)}"
                    )
                )
            }

            // 2. Análisis proactivo de Suscripciones (Fase 3)
            val totalSubMonto = subscriptions.sumOf {
                if (it.period.equals("Anual", ignoreCase = true)) it.amount / 12.0 else it.amount
            }
            if (totalSubMonto > 0 && balance.monthlyIncome > 0) {
                val porcentaje = (totalSubMonto / balance.monthlyIncome) * 100
                if (porcentaje > 15) {
                    insights.add(
                        Insight(
                            title = "Alto Impacto de Suscripciones",
                            description = "Los cargos recurrentes representan el ${String.format("%.1f", porcentaje)}% de tus ingresos. Evalúa cancelar servicios duplicados.",
                            type = InsightType.WARNING,
                            valueLabel = "$${String.format("%.2f", totalSubMonto)}/mes"
                        )
                    )
                } else {
                    insights.add(
                        Insight(
                            title = "Gastos Fijos Optimizados",
                            description = "Tus suscripciones consumen menos del 15% de tu presupuesto. Excelente control de membresías fijas.",
                            type = InsightType.SUCCESS,
                            valueLabel = "$${String.format("%.2f", totalSubMonto)}/mes"
                        )
                    )
                }
            }

            // 3. Identificación del mayor centro de costos
            val mayorGasto = stats.maxByOrNull { it.totalAmount }
            if (mayorGasto != null && mayorGasto.totalAmount > 0) {
                insights.add(
                    Insight(
                        title = "Mayor Foco de Consumo",
                        description = "La categoría '${mayorGasto.category}' lidera tus egresos actuales. Monitorea esta categoría para optimizar tu capital.",
                        type = InsightType.INFO,
                        valueLabel = mayorGasto.category
                    )
                )
            }

            // 4. Fallback de educación financiera estándar
            insights.add(
                Insight(
                    title = "Regla de Oro 50/30/20",
                    description = "Intenta distribuir tus ingresos: 50% para necesidades esenciales, 30% para entretenimiento y un 20% directo al ahorro.",
                    type = InsightType.INFO
                )
            )

            insights
        }
    }
}