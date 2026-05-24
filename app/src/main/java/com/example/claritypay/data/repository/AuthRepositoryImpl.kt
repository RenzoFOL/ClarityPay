package com.example.claritypay.data.repository

import com.example.claritypay.core.common.AppResult
import com.example.claritypay.data.local.dao.SubscriptionDao
import com.example.claritypay.data.local.dao.TransactionDao
import com.example.claritypay.data.local.dao.UserDao
import com.example.claritypay.data.local.entity.SubscriptionEntity
import com.example.claritypay.data.local.entity.TransactionEntity
import com.example.claritypay.data.local.entity.UserEntity
import com.example.claritypay.domain.models.User
import com.example.claritypay.domain.repository.AuthRepository
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val transactionDao: TransactionDao,
    private val subscriptionDao: SubscriptionDao
) : AuthRepository {

    override fun observeCurrentUser(): Flow<User?> =
        userDao.observeActiveUser().map { entity -> entity?.toDomain() }

    @Transaction
    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): AppResult<User> {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            return AppResult.Error("Completa todos los campos para crear tu cuenta.")
        }

        val existingUser = userDao.getUserByEmail(email.trim())
        if (existingUser != null) {
            return AppResult.Error("Ya existe una cuenta con este correo.")
        }

        userDao.logoutAll()
        val newUserId = userDao.insert(
            UserEntity(
                fullName = fullName.trim(),
                email = email.trim(),
                password = password.trim(),
                isLoggedIn = true
            )
        )

        if (email.trim() == "demo@claritypay.app") {
            seedDemoTransactions(newUserId)
            seedDemoSubscriptions(newUserId)
        }

        val user = userDao.getUserById(newUserId)?.toDomain()
            ?: return AppResult.Error("No se pudo crear la cuenta.")
        return AppResult.Success(user)
    }

    override suspend fun login(email: String, password: String): AppResult<User> {
        val user = userDao.getUserByEmail(email.trim())
            ?: return AppResult.Error("No encontramos una cuenta con ese correo.")
        if (user.password != password.trim()) {
            return AppResult.Error("La contraseña no es correcta.")
        }
        userDao.logoutAll()
        userDao.markLoggedIn(user.id)
        return AppResult.Success(user.toDomain())
    }

    override suspend fun logout() {
        userDao.logoutAll()
    }

    // --- MÉTODOS FASE 2 ---

    override suspend fun updateProfile(user: User): AppResult<Unit> {
        return try {
            val currentEntity = userDao.getUserById(user.id)
            if (currentEntity != null) {
                // Actualizamos nombre, email y contraseña según los cambios de la UI
                userDao.update(
                    currentEntity.copy(
                        fullName = user.fullName,
                        email = user.email,
                        password = user.password,
                        bio = user.bio,
                        profileImageUrl = user.profileImageUrl
                    )
                )
                AppResult.Success(Unit)
            } else {
                AppResult.Error("Usuario no encontrado.")
            }
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Error al actualizar el perfil.")
        }
    }

    override suspend fun resetPassword(email: String, newPassword: String): AppResult<Unit> {
        return try {
            if (newPassword.isBlank()) return AppResult.Error("La contraseña no puede estar vacía.")

            val rowsAffected = userDao.updatePasswordByEmail(email.trim(), newPassword.trim())
            if (rowsAffected > 0) {
                AppResult.Success(Unit)
            } else {
                AppResult.Error("No se encontró una cuenta con ese correo.")
            }
        } catch (e: Exception) {
            AppResult.Error(e.message ?: "Error al recuperar la contraseña.")
        }
    }

    // --- NUEVO MÉTODO FASE 6 (ELIMINAR CUENTA) ---

    @Transaction
    override suspend fun deleteAccount(userId: Long) {
        // Borramos al usuario
        userDao.deleteUserById(userId)

        // Es una buena práctica limpiar también sus transacciones y suscripciones
        // para que no queden datos "huérfanos" en la base de datos.
        // Asegúrate de tener estos métodos en tus DAOs o puedes comentarlos si usas CASCADE en Room.
        /*
        transactionDao.deleteAllByUserId(userId)
        subscriptionDao.deleteAllByUserId(userId)
        */
    }

    // ----------------------------

    override suspend fun seedDemoUserIfNeeded() {
        val existingDemo = userDao.getUserByEmail("demo@claritypay.app")
        if (existingDemo != null) {
            seedDemoTransactions(existingDemo.id)
            seedDemoSubscriptions(existingDemo.id)
            return
        }

        if (userDao.countUsers() > 0) return

        val demoUserId = userDao.insert(
            UserEntity(
                fullName = "Demo ClarityPay",
                email = "demo@claritypay.app",
                password = "123456",
                isLoggedIn = false
            )
        )
        seedDemoTransactions(demoUserId)
        seedDemoSubscriptions(demoUserId)
    }

    private suspend fun seedDemoTransactions(userId: Long) {
        if (transactionDao.countByUser(userId) > 0) return
        transactionDao.insertAll(
            listOf(
                TransactionEntity(userId = userId, title = "Nómina abril", category = "Ingreso", amount = 2450.0, type = "INCOME", dateLabel = "Hoy"),
                TransactionEntity(userId = userId, title = "Supermercado", category = "Casa", amount = 78.35, type = "EXPENSE", dateLabel = "Hoy"),
                TransactionEntity(userId = userId, title = "Transporte", category = "Movilidad", amount = 14.20, type = "EXPENSE", dateLabel = "Ayer"),
                TransactionEntity(userId = userId, title = "Café", category = "Personal", amount = 6.50, type = "EXPENSE", dateLabel = "Ayer"),
                TransactionEntity(userId = userId, title = "Internet", category = "Servicios", amount = 31.99, type = "EXPENSE", dateLabel = "Lunes"),
                TransactionEntity(userId = userId, title = "Streaming", category = "Entretenimiento", amount = 12.99, type = "EXPENSE", dateLabel = "Domingo")
            )
        )
    }

    private suspend fun seedDemoSubscriptions(userId: Long) {
        if (subscriptionDao.countByUser(userId) > 0) return
        subscriptionDao.insertAll(
            listOf(
                SubscriptionEntity(userId = userId, name = "Netflix", amount = 15.49, category = "Entretenimiento", nextPaymentDate = "2026-05-18", period = "Mensual"),
                SubscriptionEntity(userId = userId, name = "Internet hogar", amount = 31.99, category = "Servicios", nextPaymentDate = "2026-05-22", period = "Mensual"),
                SubscriptionEntity(userId = userId, name = "Almacenamiento cloud", amount = 19.99, category = "Servicios", nextPaymentDate = "2026-06-05", period = "Anual")
            )
        )
    }
}

// Se añade el mapeo de password para que el ViewModel pueda leerla y modificarla en la UI
private fun UserEntity.toDomain(): User = User(
    id = id,
    fullName = fullName,
    email = email,
    password = password,
    bio = bio,
    profileImageUrl = profileImageUrl
)