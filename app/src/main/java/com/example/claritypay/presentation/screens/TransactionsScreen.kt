package com.example.claritypay.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
import com.example.claritypay.presentation.components.AddTransactionDialog
import com.example.claritypay.presentation.components.AppScaffold
import com.example.claritypay.presentation.components.InfoMessageCard
import com.example.claritypay.presentation.components.TransactionCard
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.TransactionsViewModel

@Composable
fun TransactionsScreenRoute() {
    val app = LocalContext.current.applicationContext as ClarityPayApp
    val viewModel: TransactionsViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddTransaction by remember { mutableStateOf(false) }
    AppScaffold(title = "Transacciones") {
        Button(onClick = { showAddTransaction = true }) {
            Text("Nuevo movimiento")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (state.transactions.isEmpty()) {
            InfoMessageCard("Todavia no hay movimientos para mostrar.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.transactions.forEach { transaction ->
                    TransactionCard(transaction)
                }
            }
        }
    }

    if (showAddTransaction) {
        AddTransactionDialog(
            title = "Nuevo movimiento",
            defaultType = "EXPENSE",
            allowTypeSelection = true,
            onDismiss = { showAddTransaction = false },
            onConfirm = { title, amount, category, dateLabel, type ->
                viewModel.addTransaction(title, amount, category, dateLabel, type)
                showAddTransaction = false
            }
        )
    }
}
