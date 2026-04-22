package com.example.claritypay.di

import android.content.Context
import androidx.room.Room
import com.example.claritypay.data.local.AppDatabase
import com.example.claritypay.data.repository.AuthRepositoryImpl
import com.example.claritypay.data.repository.FinanceRepositoryImpl
import com.example.claritypay.domain.usecases.CreateAccountUseCase
import com.example.claritypay.domain.usecases.GetBalanceUseCase
import com.example.claritypay.domain.usecases.GetCurrentUserUseCase
import com.example.claritypay.domain.usecases.GetExpensesUseCase
import com.example.claritypay.domain.usecases.GetRecentTransactionsUseCase
import com.example.claritypay.domain.usecases.GetTransactionsUseCase
import com.example.claritypay.domain.usecases.LoginUseCase
import com.example.claritypay.domain.usecases.LogoutUseCase
import com.example.claritypay.domain.usecases.SeedDemoDataUseCase

class AppContainer(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "clarity_pay.db"
    ).fallbackToDestructiveMigration().build()

    private val authRepository = AuthRepositoryImpl(
        userDao = database.userDao(),
        transactionDao = database.transactionDao()
    )

    private val financeRepository = FinanceRepositoryImpl(
        transactionDao = database.transactionDao()
    )

    val createAccountUseCase = CreateAccountUseCase(authRepository)
    val loginUseCase = LoginUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
    val getBalanceUseCase = GetBalanceUseCase(financeRepository)
    val getExpensesUseCase = GetExpensesUseCase(financeRepository)
    val getRecentTransactionsUseCase = GetRecentTransactionsUseCase(financeRepository)
    val getTransactionsUseCase = GetTransactionsUseCase(financeRepository)
    val seedDemoDataUseCase = SeedDemoDataUseCase(authRepository)
}
