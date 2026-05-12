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
                    getExpensesUseCase = container.getExpensesUseCase
                ) as T
            }
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) -> {
                TransactionsViewModel(
                    getCurrentUserUseCase = container.getCurrentUserUseCase,
                    getTransactionsUseCase = container.getTransactionsUseCase
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
                    updateProfileUseCase = container.updateProfileUseCase
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

            else -> error("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}