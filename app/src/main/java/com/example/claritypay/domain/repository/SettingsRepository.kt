package com.example.claritypay.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkModeEnabled: Flow<Boolean>
    val areNotificationsEnabled: Flow<Boolean>

    suspend fun toggleDarkMode(enabled: Boolean)
    suspend fun toggleNotifications(enabled: Boolean)
}