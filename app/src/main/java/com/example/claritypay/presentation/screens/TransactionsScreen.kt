package com.example.claritypay.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.claritypay.ClarityPayApp
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
    AppScaffold(title = "Transacciones") {
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
}
