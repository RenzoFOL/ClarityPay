package com.example.claritypay.di

import android.content.Context
import com.example.claritypay.data.local.AppDatabase
import com.example.claritypay.data.repository.AuthRepositoryImpl
import com.example.claritypay.data.repository.FinanceRepositoryImpl
import com.example.claritypay.domain.repository.AuthRepository
import com.example.claritypay.domain.repository.FinanceRepository
import com.example.claritypay.domain.usecases.*

interface AppContainer {
    val authRepository: AuthRepository
    val financeRepository: FinanceRepository

    val getCurrentUserUseCase: GetCurrentUserUseCase
    val loginUseCase: LoginUseCase
    val createAccountUseCase: CreateAccountUseCase
    val logoutUseCase: LogoutUseCase
    val getBalanceUseCase: GetBalanceUseCase
    val getRecentTransactionsUseCase: GetRecentTransactionsUseCase
    val getTransactionsUseCase: GetTransactionsUseCase
    val getExpensesUseCase: GetExpensesUseCase
    val seedDemoDataUseCase: SeedDemoDataUseCase
    val updateProfileUseCase: UpdateProfileUseCase
    val resetPasswordUseCase: ResetPasswordUseCase
    val getStatisticsUseCase: GetStatisticsUseCase
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(database.userDao(), database.transactionDao())
    }

    override val financeRepository: FinanceRepository by lazy {
        FinanceRepositoryImpl(database.transactionDao())
    }

    override val getCurrentUserUseCase: GetCurrentUserUseCase by lazy { GetCurrentUserUseCase(authRepository) }
    override val loginUseCase: LoginUseCase by lazy { LoginUseCase(authRepository) }
    override val createAccountUseCase: CreateAccountUseCase by lazy { CreateAccountUseCase(authRepository) }
    override val logoutUseCase: LogoutUseCase by lazy { LogoutUseCase(authRepository) }
    override val getBalanceUseCase: GetBalanceUseCase by lazy { GetBalanceUseCase(financeRepository) }
    override val getRecentTransactionsUseCase: GetRecentTransactionsUseCase by lazy { GetRecentTransactionsUseCase(financeRepository) }
    override val getTransactionsUseCase: GetTransactionsUseCase by lazy { GetTransactionsUseCase(financeRepository) }
    override val getExpensesUseCase: GetExpensesUseCase by lazy { GetExpensesUseCase(financeRepository) }
    override val seedDemoDataUseCase: SeedDemoDataUseCase by lazy { SeedDemoDataUseCase(authRepository) }

    override val updateProfileUseCase: UpdateProfileUseCase by lazy {
        UpdateProfileUseCase(authRepository)
    }

    override val resetPasswordUseCase: ResetPasswordUseCase by lazy {
        ResetPasswordUseCase(authRepository)
    }

    override val getStatisticsUseCase: GetStatisticsUseCase by lazy {
        GetStatisticsUseCase(financeRepository)
    }
}