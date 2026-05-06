package com.example.claritypay.domain.usecases

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.User
import com.example.claritypay.domain.repository.AuthRepository

class UpdateProfileUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(user: User): AppResult<Unit> {
        if (user.fullName.isBlank()) {
            return AppResult.Error("El nombre no puede estar vacío")
        }
        return repository.updateProfile(user)
    }
}