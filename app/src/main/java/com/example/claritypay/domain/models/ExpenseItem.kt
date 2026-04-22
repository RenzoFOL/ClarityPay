package com.example.claritypay.domain.models

data class ExpenseItem(
    val category: String,
    val totalAmount: Double,
    val transactionsCount: Int
)
