package com.example.claritypay.domain.models

data class PricingPlan(
    val id: String,
    val name: String,
    val price: Double,
    val period: String,
    val features: List<String>,
    val isCurrent: Boolean = false,
    val isPopular: Boolean = false
)