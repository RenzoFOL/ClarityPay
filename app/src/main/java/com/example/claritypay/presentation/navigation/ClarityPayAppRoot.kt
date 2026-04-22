package com.example.claritypay.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.claritypay.presentation.screens.ExpensesScreenRoute
import com.example.claritypay.presentation.screens.HomeScreenRoute
import com.example.claritypay.presentation.screens.LoginScreenRoute
import com.example.claritypay.presentation.screens.RegisterScreenRoute
import com.example.claritypay.presentation.screens.TransactionsScreenRoute
import com.example.claritypay.presentation.viewmodels.SessionViewModel

@Composable
fun ClarityPayAppRoot(sessionViewModel: SessionViewModel) {
    val navController = rememberNavController()
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = authenticatedDestinations.any { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }

    LaunchedEffect(sessionState.isAuthenticated, sessionState.isLoading) {
        if (sessionState.isLoading) return@LaunchedEffect
        val targetRoute = if (sessionState.isAuthenticated) AppDestination.Home.route else AppDestination.Login.route
        navController.navigate(targetRoute) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
            launchSingleTop = true
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
                                    popUpTo(AppDestination.Home.route)
                                    launchSingleTop = true
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
            composable(AppDestination.Login.route) {
                LoginScreenRoute(
                    onRegisterClick = { navController.navigate(AppDestination.Register.route) }
                )
            }
            composable(AppDestination.Register.route) {
                RegisterScreenRoute(
                    onBackToLogin = { navController.popBackStack() }
                )
            }
            composable(AppDestination.Home.route) {
                HomeScreenRoute(
                    onOpenExpenses = { navController.navigate(AppDestination.Expenses.route) },
                    onOpenTransactions = { navController.navigate(AppDestination.Transactions.route) }
                )
            }
            composable(AppDestination.Expenses.route) {
                ExpensesScreenRoute()
            }
            composable(AppDestination.Transactions.route) {
                TransactionsScreenRoute()
            }
        }
    }
}
