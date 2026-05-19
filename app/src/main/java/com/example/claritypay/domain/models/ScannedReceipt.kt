package com.example.claritypay.domain.models

data class ScannedReceipt(
    val title: String,
    val amount: Double,
    val category: String,
    val ticketType: String,
    val dateLabel: String,
    val rawText: String
)
