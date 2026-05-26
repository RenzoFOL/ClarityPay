package com.example.claritypay.domain.repository

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.domain.models.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeCurrentUser(): Flow<User?>
    suspend fun register(fullName: String, email: String, password: String): AppResult<User>
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun logout()
    suspend fun seedDemoUserIfNeeded()
    suspend fun updateProfile(user: User): AppResult<Unit>
    suspend fun resetPassword(email: String, newPassword: String): AppResult<Unit>

    suspend fun deleteAccount(userId: Long)

    suspend fun getUserByEmail(email: String): User?

    suspend fun updatePassword(email: String, newPassword: String)
}