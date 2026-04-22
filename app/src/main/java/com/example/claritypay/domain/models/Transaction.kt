package com.example.claritypay.domain.models

data class Transaction(
    val id: Long,
    val title: String,
    val category: String,
    val amount: Double,
    val type: String,
    val dateLabel: String
)
