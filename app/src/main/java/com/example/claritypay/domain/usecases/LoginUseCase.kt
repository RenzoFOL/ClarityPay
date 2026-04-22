package com.example.claritypay.domain.usecases

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.User
import com.example.claritypay.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AppResult<User> =
        authRepository.login(email, password)
}
