package com.example.claritypay.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val title: String = "",
    val icon: ImageVector? = null
) {
    data object Login : AppDestination("login")
    data object Register : AppDestination("register")
    data object ResetPassword : AppDestination("reset_password") // CU-03

    // Destinos con Barra Inferior
    data object Home : AppDestination("home", "Inicio", Icons.Default.Home)
    data object Expenses : AppDestination("expenses", "Gastos", Icons.Default.ReceiptLong)
    data object ReceiptScanner : AppDestination("receipt_scanner")
    data object Statistics : AppDestination("statistics", "Estadísticas", Icons.Default.Assessment) // CU-14
    data object Transactions : AppDestination("transactions", "Historial", Icons.Default.History)
    data object Profile : AppDestination("profile", "Perfil", Icons.Default.AccountCircle) // CU-10
    data object Settings : AppDestination("Configuración")

    data object Subscriptions : AppDestination(
        route = "subscriptions",
        title = "Suscripciones",
        icon = Icons.Default.Subscriptions // O Icons.Default.CreditCard
    )
}

// Lista que controla qué iconos se ven en la NavigationBar
val authenticatedDestinations = listOf(
    AppDestination.Home,
    AppDestination.Expenses,
    AppDestination.Statistics, // Se agrega a la barra inferior
    AppDestination.Subscriptions, // <--- AGREGADO AQUÍ
    AppDestination.Transactions,
    AppDestination.Profile      // Se agrega a la barra inferior
)
