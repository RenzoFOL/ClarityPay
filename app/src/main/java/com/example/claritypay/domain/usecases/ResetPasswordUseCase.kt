package com.example.claritypay.domain.usecases

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.repository.AuthRepository

class ResetPasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, newPassword: String): AppResult<Unit> {
        if (newPassword.length < 6) {
            return AppResult.Error("La contraseña debe tener al menos 6 caracteres")
        }
        return repository.resetPassword(email, newPassword)
    }
}