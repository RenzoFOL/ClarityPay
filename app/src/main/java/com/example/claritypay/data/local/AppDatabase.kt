package com.example.claritypay.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.claritypay.data.local.dao.SubscriptionDao
import com.example.claritypay.data.local.dao.UserDao
import com.example.claritypay.data.local.dao.TransactionDao
import com.example.claritypay.data.local.entity.SubscriptionEntity
import com.example.claritypay.data.local.entity.UserEntity
import com.example.claritypay.data.local.entity.TransactionEntity

@Database(
    entities = [UserEntity::class, TransactionEntity::class, SubscriptionEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun subscriptionDao(): SubscriptionDao // Agregado

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        // Este es el método que te faltaba
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "claritypay_db"
                )
                    .fallbackToDestructiveMigration() // Borra la BD vieja si cambias el esquema
                    .build()
                    .also { Instance = it }
            }
        }
    }
}