package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.repository.AuthRepository

class DeleteAccountUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: Long) = repository.deleteAccount(userId)
}