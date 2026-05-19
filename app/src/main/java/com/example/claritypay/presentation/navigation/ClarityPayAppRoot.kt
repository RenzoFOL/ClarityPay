package com.example.claritypay.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.example.claritypay.presentation.screens.*
import com.example.claritypay.presentation.viewmodels.SessionViewModel

@Composable
fun ClarityPayAppRoot(sessionViewModel: SessionViewModel) {
    val navController = rememberNavController()
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Control dinámico de la visibilidad de la BottomBar
    val showBottomBar = authenticatedDestinations.any { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }

    // Lógica de redirección automática por estado de sesión
    LaunchedEffect(sessionState.isAuthenticated, sessionState.isLoading) {
        if (sessionState.isLoading) return@LaunchedEffect

        if (!sessionState.isAuthenticated) {
            // Si no está logueado y no está en pantallas de "Auth", mandar a Login
            val authRoutes = listOf(AppDestination.Login.route, AppDestination.Register.route, AppDestination.ResetPassword.route)
            if (currentDestination?.route !in authRoutes) {
                navController.navigate(AppDestination.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else {
            // Si se acaba de loguear y está en Login/Register, mandarlo a Home
            if (currentDestination?.route == AppDestination.Login.route ||
                currentDestination?.route == AppDestination.Register.route) {
                navController.navigate(AppDestination.Home.route) {
                    popUpTo(AppDestination.Login.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    authenticatedDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                            onClick = {
                                navController.navigate(destination.route) {
                                    // Evita acumular copias de la misma pantalla en la pila
                                    popUpTo(AppDestination.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon!!, contentDescription = destination.title) },
                            label = { Text(destination.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- PANTALLAS DE AUTENTICACIÓN ---
            composable(AppDestination.Login.route) {
                LoginScreenRoute(
                    onRegisterClick = { navController.navigate(AppDestination.Register.route) },
                    onForgotPasswordClick = { navController.navigate(AppDestination.ResetPassword.route) }
                )
            }
            composable(AppDestination.Register.route) {
                RegisterScreenRoute(
                    onBackToLogin = { navController.popBackStack() }
                )
            }
            composable(AppDestination.ResetPassword.route) {
                ResetPasswordScreenRoute(
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            // --- PANTALLAS PRINCIPALES (BARRA INFERIOR) ---
            composable(AppDestination.Home.route) {
                HomeScreenRoute(
                    onOpenExpenses = { navController.navigate(AppDestination.Expenses.route) },
                    onOpenTransactions = { navController.navigate(AppDestination.Transactions.route) },
                    onOpenProfile = { navController.navigate(AppDestination.Profile.route) },
                    onOpenStatistics = { navController.navigate(AppDestination.Statistics.route) }
                )
            }
            composable(AppDestination.Expenses.route) {
                ExpensesScreenRoute(
                    onScanReceipt = { navController.navigate(AppDestination.ReceiptScanner.route) }
                )
            }
            composable(AppDestination.ReceiptScanner.route) {
                ReceiptScannerScreenRoute(
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.navigate(AppDestination.Expenses.route) {
                            popUpTo(AppDestination.ReceiptScanner.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(AppDestination.Statistics.route) {
                StatisticsScreenRoute()
            }
            composable(AppDestination.Transactions.route) {
                TransactionsScreenRoute()
            }
            composable(AppDestination.Profile.route) {
                ProfileScreenRoute()
            }
            // --- DESTINO FASE 3: Suscripciones ---
            composable(AppDestination.Subscriptions.route) {
                // Llamamos a la función Route que creamos en SubscriptionsScreen.kt
                SubscriptionsScreenRoute()
            }
        }
    }
}
