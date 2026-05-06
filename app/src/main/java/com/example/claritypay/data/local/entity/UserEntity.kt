package com.example.claritypay.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val isLoggedIn: Boolean = false,

    val bio: String? = null,           // Para una breve descripción (puede ser nulo al registrarse)
    val profileImageUrl: String? = null // Ruta local de la imagen de perfil (puede ser nulo)
)