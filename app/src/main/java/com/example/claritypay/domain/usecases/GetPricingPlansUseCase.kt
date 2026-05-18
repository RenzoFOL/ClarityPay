package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.PricingPlan

class GetPricingPlansUseCase {
    operator fun invoke(): List<PricingPlan> {
        return listOf(
            PricingPlan(
                id = "free",
                name = "Plan Esencial",
                price = 0.0,
                period = "Siempre gratis",
                features = listOf(
                    "Registro manual ilimitado de movimientos",
                    "Estadísticas básicas por categoría",
                    "Hasta 3 suscripciones activas",
                    "Cifrado y persistencia local Room"
                ),
                isCurrent = false // <--- Cambiado a false (Ya no es el inicial)
            ),
            PricingPlan(
                id = "pro",
                name = "Plan Clarity Pro",
                price = 69.0,
                period = "Mes",
                features = listOf(
                    "Suscripciones y recordatorios ilimitados",
                    "Insights y consejos con Inteligencia Artificial",
                    "Exportación de reportes financieros a PDF/Excel",
                    "Gráficos avanzados interactivos y modo oscuro"
                ),
                isCurrent = true, // <--- ¡AHORA ESTE TIENE EL PUESTO DE PLAN ACTUAL!
                isPopular = true
            ),
            PricingPlan(
                id = "premium",
                name = "Plan Premium Anual",
                price = 690.0,
                period = "Año",
                features = listOf(
                    "Todo lo incluido en el Plan Pro",
                    "Ahorro del 33% en facturación anualizada",
                    "Sincronización multi-dispositivo en tiempo real",
                    "Soporte prioritario 24/7 de ingeniería"
                )
            )
        )
    }
}