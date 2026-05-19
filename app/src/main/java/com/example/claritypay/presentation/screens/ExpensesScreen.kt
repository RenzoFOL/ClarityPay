package com.example.claritypay.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
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
import com.example.claritypay.presentation.components.ExpenseCard
import com.example.claritypay.presentation.components.InfoMessageCard
import com.example.claritypay.presentation.viewmodels.AppViewModelFactory
import com.example.claritypay.presentation.viewmodels.ExpensesViewModel

@Composable
fun ExpensesScreenRoute(
    onScanReceipt: () -> Unit
) {
    val app = LocalContext.current.applicationContext as ClarityPayApp
    val viewModel: ExpensesViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddExpense by remember { mutableStateOf(false) }
    AppScaffold(title = "Gastos") {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { showAddExpense = true }, modifier = Modifier.weight(1f)) {
                Text("Nuevo gasto")
            }
            Button(onClick = onScanReceipt, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Escanear ticket")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        InfoMessageCard("Agrupados por categoria para mantener la vista simple en esta primera version.")
        Spacer(modifier = Modifier.height(16.dp))
        if (state.expenses.isEmpty()) {
            InfoMessageCard("Todavia no hay gastos registrados.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.expenses.forEach { expense ->
                    ExpenseCard(expense)
                }
            }
        }
    }

    if (showAddExpense) {
        AddTransactionDialog(
            title = "Nuevo gasto",
            defaultType = "EXPENSE",
            allowTypeSelection = false,
            onDismiss = { showAddExpense = false },
            onConfirm = { title, amount, category, dateLabel, _ ->
                viewModel.addExpense(title, amount, category, dateLabel)
                showAddExpense = false
            }
        )
    }
}
