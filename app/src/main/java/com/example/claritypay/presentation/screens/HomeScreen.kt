package com.example.claritypay.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.presentation.components.AppScaffold
import com.example.claritypay.presentation.components.BalanceCard
import com.example.claritypay.presentation.components.InfoMessageCard
import com.example.claritypay.presentation.components.SectionHeader
import com.example.claritypay.presentation.components.TransactionCard
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.HomeViewModel

@Composable
fun HomeScreenRoute(
    onOpenExpenses: () -> Unit,
    onOpenTransactions: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenStatistics: () -> Unit
) {
    val app = LocalContext.current.applicationContext as ClarityPayApp
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AppScaffold(
        title = "Inicio",
        action = {
            IconButton(onClick = viewModel::logout) {
                Icon(Icons.Outlined.Logout, contentDescription = "Cerrar sesion")
            }
        }
    ) {
        Text("Hola, ${state.userName}", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Resumen rapido de tu dinero este mes.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(18.dp))
        state.balance?.let { BalanceCard(it) } ?: InfoMessageCard("Aun no hay balance disponible.")
        Spacer(modifier = Modifier.height(22.dp))
        SectionHeader("Gastos", "Ver todos", onOpenExpenses)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onOpenExpenses, modifier = Modifier.fillMaxWidth()) {
            Text("Abrir vista de gastos")
        }
        Spacer(modifier = Modifier.height(22.dp))
        SectionHeader("Transacciones recientes", "Abrir lista", onOpenTransactions)
        Spacer(modifier = Modifier.height(10.dp))
        if (state.recentTransactions.isEmpty()) {
            InfoMessageCard("No hay movimientos recientes por ahora.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.recentTransactions.forEach { transaction ->
                    TransactionCard(transaction)
                }
            }
        }
    }
}
