package com.example.claritypay.di

import android.content.Context
import com.example.claritypay.data.local.AppDatabase
import com.example.claritypay.data.repository.AuthRepositoryImpl
import com.example.claritypay.data.repository.FinanceRepositoryImpl
import com.example.claritypay.data.repository.SettingsRepositoryImpl
import com.example.claritypay.data.repository.SubscriptionRepositoryImpl
import com.example.claritypay.domain.repository.AuthRepository
import com.example.claritypay.domain.repository.FinanceRepository
import com.example.claritypay.domain.repository.SettingsRepository
import com.example.claritypay.domain.repository.SubscriptionRepository
import com.example.claritypay.domain.usecases.*

interface AppContainer {
    val authRepository: AuthRepository
    val financeRepository: FinanceRepository
    val subscriptionRepository: SubscriptionRepository
    val settingsRepository: SettingsRepository

    val getCurrentUserUseCase: GetCurrentUserUseCase
    val loginUseCase: LoginUseCase
    val createAccountUseCase: CreateAccountUseCase
    val logoutUseCase: LogoutUseCase
    val getBalanceUseCase: GetBalanceUseCase
    val getRecentTransactionsUseCase: GetRecentTransactionsUseCase
    val getTransactionsUseCase: GetTransactionsUseCase
    val getExpensesUseCase: GetExpensesUseCase
    val addTransactionUseCase: AddTransactionUseCase
    val seedDemoDataUseCase: SeedDemoDataUseCase
    val updateProfileUseCase: UpdateProfileUseCase
    val resetPasswordUseCase: ResetPasswordUseCase
    val getStatisticsUseCase: GetStatisticsUseCase

    // --- NUEVOS USE CASES FASE 3 ---
    val getSubscriptionsUseCase: GetSubscriptionsUseCase
    val addSubscriptionUseCase: AddSubscriptionUseCase
    val updateSubscriptionUseCase: UpdateSubscriptionUseCase
    val deleteSubscriptionUseCase: DeleteSubscriptionUseCase

    //AGREGADOS LOS CONTRATOS PARA LA FASE 4 ---
    val getInsightsUseCase: GetInsightsUseCase
    val getPricingPlansUseCase: GetPricingPlansUseCase

    // --- FASE 5 ---
    val getSettingsUseCase: GetSettingsUseCase
    val updateSettingsUseCase: UpdateSettingsUseCase
    val deleteAccountUseCase: DeleteAccountUseCase
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(database.userDao(), database.transactionDao(), database.subscriptionDao())
    }

    override val financeRepository: FinanceRepository by lazy {
        FinanceRepositoryImpl(database.transactionDao(), database.subscriptionDao())
    }

    // --- REPOSITORIO FASE 3 ---
    override val subscriptionRepository: SubscriptionRepository by lazy {
        SubscriptionRepositoryImpl(database.subscriptionDao())
    }

    // --- REPOSITORIO FASE 5 ---
    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }

    override val getCurrentUserUseCase: GetCurrentUserUseCase by lazy { GetCurrentUserUseCase(authRepository) }
    override val loginUseCase: LoginUseCase by lazy { LoginUseCase(authRepository) }
    override val createAccountUseCase: CreateAccountUseCase by lazy { CreateAccountUseCase(authRepository) }
    override val logoutUseCase: LogoutUseCase by lazy { LogoutUseCase(authRepository) }
    override val getBalanceUseCase: GetBalanceUseCase by lazy { GetBalanceUseCase(financeRepository) }
    override val getRecentTransactionsUseCase: GetRecentTransactionsUseCase by lazy { GetRecentTransactionsUseCase(financeRepository) }
    override val getTransactionsUseCase: GetTransactionsUseCase by lazy { GetTransactionsUseCase(financeRepository) }
    override val getExpensesUseCase: GetExpensesUseCase by lazy { GetExpensesUseCase(financeRepository) }
    override val addTransactionUseCase: AddTransactionUseCase by lazy { AddTransactionUseCase(financeRepository) }
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

    // --- INSTANCIAS FASE 3 ---
    override val getSubscriptionsUseCase by lazy {
        GetSubscriptionsUseCase(subscriptionRepository)
    }
    override val addSubscriptionUseCase by lazy {
        AddSubscriptionUseCase(subscriptionRepository)
    }
    override val updateSubscriptionUseCase by lazy {
        UpdateSubscriptionUseCase(subscriptionRepository)
    }
    override val deleteSubscriptionUseCase by lazy {
        DeleteSubscriptionUseCase(subscriptionRepository)
    }

    // --- NUEVAS INSTANCIAS AGREGADAS PARA FASE 4 ---
    override val getInsightsUseCase by lazy {
        GetInsightsUseCase(financeRepository, subscriptionRepository)
    }
    override val getPricingPlansUseCase by lazy {
        GetPricingPlansUseCase()
    }

    // --- INSTANCIAS FASE 5 ---
    override val getSettingsUseCase: GetSettingsUseCase by lazy {
        GetSettingsUseCase(settingsRepository)
    }
    override val updateSettingsUseCase: UpdateSettingsUseCase by lazy {
        UpdateSettingsUseCase(settingsRepository)
    }
    override val deleteAccountUseCase by lazy {
        DeleteAccountUseCase(authRepository) }
}