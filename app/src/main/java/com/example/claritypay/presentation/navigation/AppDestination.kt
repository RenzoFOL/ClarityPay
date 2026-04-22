package com.example.claritypay.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object Login : AppDestination("login", "Iniciar sesion")
    data object Register : AppDestination("register", "Crear cuenta")
    data object Home : AppDestination("home", "Balance", Icons.Outlined.AccountBalanceWallet)
    data object Expenses : AppDestination("expenses", "Gastos", Icons.Outlined.ListAlt)
    data object Transactions : AppDestination("transactions", "Movimientos", Icons.Outlined.ReceiptLong)
}

val authenticatedDestinations = listOf(
    AppDestination.Home,
    AppDestination.Expenses,
    AppDestination.Transactions
)
