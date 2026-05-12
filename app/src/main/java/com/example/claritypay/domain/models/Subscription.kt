package com.example.claritypay.domain.models

data class Subscription(
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val amount: Double,
    val category: String,
    val nextPaymentDate: String, // Formato "yyyy-MM-dd"
    val period: String // "Mensual", "Anual"
)