package com.example.claritypay.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.claritypay.data.local.dao.TransactionDao
import com.example.claritypay.data.local.dao.UserDao
import com.example.claritypay.data.local.entity.TransactionEntity
import com.example.claritypay.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
}
