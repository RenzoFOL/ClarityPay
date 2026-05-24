package com.example.claritypay.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.claritypay.di.AppContainer

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SessionViewModel::class.java) -> {
                SessionViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    seedDemoDataUseCase = container.seedDemoDataUseCase
                ) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(
                    createAccountUseCase = container.createAccountUseCase,
                    loginUseCase = container.loginUseCase
                ) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    getBalanceUseCase = container.getBalanceUseCase,
                    getRecentTransactionsUseCase = container.getRecentTransactionsUseCase,
                    logoutUseCase = container.logoutUseCase
                ) as T
            }
            modelClass.isAssignableFrom(ExpensesViewModel::class.java) -> {
                ExpensesViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    getExpensesUseCase = container.getExpensesUseCase,
                    addTransactionUseCase = container.addTransactionUseCase
                ) as T
            }
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) -> {
                TransactionsViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    getTransactionsUseCase = container.getTransactionsUseCase,
                    addTransactionUseCase = container.addTransactionUseCase
                ) as T
            }
            modelClass.isAssignableFrom(ReceiptScannerViewModel::class.java) -> {
                ReceiptScannerViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    addTransactionUseCase = container.addTransactionUseCase
                ) as T
            }
            modelClass.isAssignableFrom(SubscriptionsViewModel::class.java) -> {
                SubscriptionsViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    getSubscriptionsUseCase = container.getSubscriptionsUseCase,
                    addSubscriptionUseCase = container.addSubscriptionUseCase,
                    updateSubscriptionUseCase = container.updateSubscriptionUseCase,
                    deleteSubscriptionUseCase = container.deleteSubscriptionUseCase
                ) as T
            }

            // --- NUEVOS VIEWMODELS FASE 2 ---

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    updateProfileUseCase = container.updateProfileUseCase,
                    deleteAccountUseCase = container.deleteAccountUseCase
                ) as T

            }
            modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> {
                StatisticsViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    getStatisticsUseCase = container.getStatisticsUseCase
                ) as T
            }
            modelClass.isAssignableFrom(ResetPasswordViewModel::class.java) -> {
                ResetPasswordViewModel(
                    resetPasswordUseCase = container.resetPasswordUseCase
                ) as T
            }

            // Agrega estos dos bloques dentro del "when" de tu AppViewModelFactory:
            modelClass.isAssignableFrom(InsightsViewModel::class.java) -> {
                InsightsViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    getInsightsUseCase = container.getInsightsUseCase
                ) as T
            }
            modelClass.isAssignableFrom(PlansViewModel::class.java) -> {
                PlansViewModel(
                    getPricingPlansUseCase = container.getPricingPlansUseCase
                ) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    getSettingsUseCase = container.getSettingsUseCase,
                    updateSettingsUseCase = container.updateSettingsUseCase
                ) as T
            }

            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
