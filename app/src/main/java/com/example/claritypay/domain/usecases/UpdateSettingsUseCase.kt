package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.repository.SettingsRepository

class UpdateSettingsUseCase(
    private val repository: SettingsRepository
) {
    suspend fun toggleDarkMode(enabled: Boolean) = repository.toggleDarkMode(enabled)
    suspend fun toggleNotifications(enabled: Boolean) = repository.toggleNotifications(enabled)
}