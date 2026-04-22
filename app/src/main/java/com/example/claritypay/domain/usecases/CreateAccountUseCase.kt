package com.example.claritypay.domain.usecases

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.User
import com.example.claritypay.domain.repository.AuthRepository

class CreateAccountUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String
    ): AppResult<User> = authRepository.register(fullName, email, password)
}
