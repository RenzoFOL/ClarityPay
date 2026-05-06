package com.example.claritypay.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val category: String, //(Estadísticas)
    val amount: Double,
    val type: String,
    val dateLabel: String, // Lo que el usuario ve (ej: "Hoy", "25 Oct")

    val timestamp: Long = System.currentTimeMillis() // Para lógica de filtros y ordenamiento
)