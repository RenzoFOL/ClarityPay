package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.models.User
import com.example.claritypay.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> = authRepository.observeCurrentUser()
}
