package com.example.claritypay.domain.models

data class User(
    val id: Long,
    val fullName: String,
    val email: String,
    val bio: String? = null,
    val profileImageUrl: String? = null
)