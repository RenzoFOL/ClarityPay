package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}
