package com.example.claritypay.domain.models

enum class InsightType {
    SUCCESS,    // Logros financieros positivos
    WARNING,    // Alertas de exceso de consumo
    INFO        // Consejos de educación financiera
}

data class Insight(
    val title: String,
    val description: String,
    val type: InsightType,
    val valueLabel: String = ""
)