package com.example.claritypay.domain.usecases

import com.example.claritypay.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(
    private val repository: SettingsRepository
) {
    fun observeDarkMode(): Flow<Boolean> = repository.isDarkModeEnabled
    fun observeNotifications(): Flow<Boolean> = repository.areNotificationsEnabled
}